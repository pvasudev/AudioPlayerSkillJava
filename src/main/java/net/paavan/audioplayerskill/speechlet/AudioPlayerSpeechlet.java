package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletRequestModule;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.StopDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.*;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
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
import java.util.Optional;

@Slf4j
public class AudioPlayerSpeechlet implements SpeechletV2, AudioPlayer {
    private static final String NOW_PLAYING_DO_NOT_KNOW_MESSAGE = "The context was empty, so Audio Player isn't playing anything!";
    private static final String NOW_PLAYING_TITLE = "Currently Playing";
    private static final String NOW_PLAYING_CARD_URL_DELIMITER = "\n\nfrom the URL\n\n";

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
            case "NowPlayingIntent":
                return getNowPlayingResponse(requestEnvelope);
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
     * Creates and returns the response to {@code NowPlayingIntent}. Constructs an OutputSpeech message & a simple card using the
     * <i>currently playing</i> token from {@link AudioPlayerState} if present, or the {@link #NOW_PLAYING_DO_NOT_KNOW_MESSAGE} otherwise.
     *
     * @param requestEnvelope the intent request envelope
     * @return response to {@code NowPlayingIntent}
     */
    private SpeechletResponse getNowPlayingResponse(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> currentlyPlaying = getCurrentlyPlayingToken(requestEnvelope);
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        Optional<SimpleCard> card = Optional.empty();
        String message;

        if (currentlyPlaying.isPresent()) {
            message = getDisplayableSongPlayed(currentlyPlaying.get());
            card = Optional.of(new SimpleCard());
            card.get().setTitle(NOW_PLAYING_TITLE);
            card.get().setContent(new StringBuilder()
                    .append(message)
                    .append(NOW_PLAYING_CARD_URL_DELIMITER)
                    .append(currentlyPlaying.get())
                    .toString());
        } else {
            message = NOW_PLAYING_DO_NOT_KNOW_MESSAGE;
        }

        speech.setText(message);
        return card.isPresent() ? SpeechletResponse.newTellResponse(speech, card.get()) : SpeechletResponse.newTellResponse(speech);
    }

    /**
     * Returns the currently playing token from {@link Context} if present, empty optional otherwise.
     *
     * @param requestEnvelope the intent request envelope
     * @return the currently playing token from {@link Context} if present, empty optional otherwise
     */
    private Optional<String> getCurrentlyPlayingToken(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> currentlyPlaying = Optional.empty();

        if (requestEnvelope.getContext().hasState(AudioPlayerInterface.class)) {
            AudioPlayerState audioPlayerState = requestEnvelope.getContext().getState(AudioPlayerInterface.class, AudioPlayerState.class);
            currentlyPlaying = Optional.ofNullable(audioPlayerState.getToken());
        }
        return currentlyPlaying;
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
