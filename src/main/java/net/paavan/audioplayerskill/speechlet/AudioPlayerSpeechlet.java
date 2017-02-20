package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletRequestModule;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioItem;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioPlayer;
import com.amazon.speech.speechlet.interfaces.audioplayer.Stream;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.StopDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.*;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.PlaybackManager;
import net.paavan.audioplayerskill.event.SpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;

import java.net.URLDecoder;
import java.util.Collections;

@Slf4j
public class AudioPlayerSpeechlet implements SpeechletV2, AudioPlayer {
    private static final ObjectMapper MAPPER = new ObjectMapper() {{
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // For context object
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        registerModule(new SpeechletRequestModule());
    }};

    private final PlaybackManager playbackManager;
    private final SpeechletEventListener speechletEventManager;

    public AudioPlayerSpeechlet(final PlaybackManager playbackManager, final SpeechletEventManager speechletEventManager) {
        this.playbackManager = playbackManager;
        this.speechletEventManager = speechletEventManager;
    }

    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        logSpeechletReqeust("onSessionStarted", requestEnvelope);
    }

    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        logSpeechletReqeust("onLaunch", requestEnvelope);
        speechletEventManager.onLaunch();
        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        logSpeechletReqeust("onIntent", requestEnvelope);

        // TODO: Clean this up
        switch(requestEnvelope.getRequest().getIntent().getName()) {
            case "AMAZON.PauseIntent":
                speechletEventManager.onPause();
                break;
            case "AMAZON.ResumeIntent":
                speechletEventManager.onResume();
                break;
            case "AMAZON.NextIntent":
                speechletEventManager.onNext();
                break;
            case "AMAZON.PreviousIntent":
                speechletEventManager.onPrevious();
                break;
            case "AMAZON.StartOverIntent":
                speechletEventManager.onStartOver();
                break;
            default:
                break;
        }

        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    private SpeechletResponse getSpeechletResponse(final PlaybackManager.PlaybackResponse playbackResponse) {
        SpeechletResponse response = new SpeechletResponse();
        response.setShouldEndSession(true);

        if (playbackResponse.getNextToken().isPresent()) {
            String url = playbackResponse.getNextToken().get();
            Stream stream = new Stream();
            stream.setUrl(url);
            stream.setToken(url);
            AudioItem audioItem = new AudioItem();
            audioItem.setStream(stream);

            PlayDirective playDirective = new PlayDirective();
            playDirective.setAudioItem(audioItem);
            playDirective.setPlayBehavior(playbackResponse.getPlayBehavior().get());
            if (playbackResponse.getExpectedToken().isPresent()) {
                stream.setExpectedPreviousToken(playbackResponse.getExpectedToken().get());
            }

            response.setDirectives(Collections.singletonList(playDirective));
//            response.setCard(getCard(url));
        } else {
            response.setDirectives(Collections.singletonList(new StopDirective()));
        }

        return response;
    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        logSpeechletReqeust("onSessionEnded", requestEnvelope);
    }

    @Override
    public SpeechletResponse onPlaybackFailed(final SpeechletRequestEnvelope<PlaybackFailedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackFailed", requestEnvelope);
        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onPlaybackFinished(final SpeechletRequestEnvelope<PlaybackFinishedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackFinished", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackNearlyFinished(final SpeechletRequestEnvelope<PlaybackNearlyFinishedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackNearlyFinished", requestEnvelope);
        speechletEventManager.onPlaybackNearlyFinished(requestEnvelope.getRequest().getToken());
        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onPlaybackStarted(final SpeechletRequestEnvelope<PlaybackStartedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackStarted", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackStopped(final SpeechletRequestEnvelope<PlaybackStoppedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackStopped", requestEnvelope);
        return null;
    }

    private void logSpeechletReqeust(final String tag, final SpeechletRequestEnvelope<?> requestEnvelope) {
        try {
            log.info(tag + " SpeechletRequestEnvelope: " +
                    MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(requestEnvelope));
        } catch (JsonProcessingException e) {
            log.error("Error serializing speechlet request", e);
        }
    }

    /**
     * Creates a {@link SimpleCard} using the URL string to produce a displayable song title.
     *
     * @param url the url of the song
     * @return a simple card
     */
    private Card getCard(String url) {
        SimpleCard card = new SimpleCard();
        card.setTitle("Next Song");
        card.setContent(getDisplayableSongPlayed(url));
        return card;
    }

    /**
     * An hacky method to etch out displayable name from the song URL.
     *
     * @param url the url of the song
     * @return displayable name
     */
    private static String getDisplayableSongPlayed(final String url) {
        String[] parts = url.split("/");
        String title = URLDecoder.decode(parts[parts.length - 1]);
        if (title.lastIndexOf(".mp3") != -1) {
            title = title.substring(0, title.lastIndexOf(".mp3"));
        }
        if (title.lastIndexOf(" @ Fmw11.com") != -1) {
            title = title.substring(0, title.lastIndexOf(" @ Fmw11.com"));
        }

        return title;
    }
}