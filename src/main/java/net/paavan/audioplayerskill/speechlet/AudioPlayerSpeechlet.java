package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletRequestModule;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioItem;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioPlayer;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioPlayerInterface;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioPlayerState;
import com.amazon.speech.speechlet.interfaces.audioplayer.Stream;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.StopDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.PlaybackFailedRequest;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.PlaybackFinishedRequest;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.PlaybackNearlyFinishedRequest;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.PlaybackStartedRequest;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.PlaybackStoppedRequest;
import com.amazon.speech.speechlet.interfaces.system.SystemInterface;
import com.amazon.speech.speechlet.interfaces.system.SystemState;
import com.amazon.speech.speechlet.services.DirectiveEnvelope;
import com.amazon.speech.speechlet.services.DirectiveEnvelopeHeader;
import com.amazon.speech.speechlet.services.DirectiveService;
import com.amazon.speech.speechlet.services.SpeakDirective;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.event.SpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.playback.PlaybackManager;
import net.paavan.audioplayerskill.settings.ContentAbstractionType;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class AudioPlayerSpeechlet implements SpeechletV2, AudioPlayer {
    private static final String NOW_PLAYING_DO_NOT_KNOW_MESSAGE = "The context was empty, so Audio Player isn't playing anything!";
    private static final String NOW_PLAYING_TITLE = "Currently Playing";
    private static final String NOW_PLAYING_CARD_URL_DELIMITER = "\n\nfrom the URL\n\n";
    private static final String SKILL_WELCOME_RESPONSE = "Welcome to Audio Player!";

    private static final ObjectMapper MAPPER = new ObjectMapper() {{
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // For context object
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        registerModule(new SpeechletRequestModule());
    }};

    private final PlaybackManager playbackManager;
    private final SpeechletEventListener speechletEventManager;
    private final DirectiveService directiveService;

    @Inject
    public AudioPlayerSpeechlet(final PlaybackManager playbackManager, final SpeechletEventManager speechletEventManager,
                                final DirectiveService directiveService) {
        this.playbackManager = playbackManager;
        this.speechletEventManager = speechletEventManager;
        this.directiveService = directiveService;
    }

    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        logSpeechletRequest("onSessionStarted", requestEnvelope);
    }

    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        logSpeechletRequest("onLaunch", requestEnvelope);
        speechletEventManager.onLaunch();
        if (requestEnvelope.getContext().hasState(SystemInterface.class)) {
            dispatchProgressiveResponse(requestEnvelope.getRequest().getRequestId(), SKILL_WELCOME_RESPONSE,
                    requestEnvelope.getContext().getState(SystemInterface.class, SystemState.class));
        }
        speechletEventManager.onInitialProgressiveDispatch();

        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        logSpeechletRequest("onIntent", requestEnvelope);

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
            case "PlayCokeStudioIntent":
                speechletEventManager.onMusicSelectionIntent(ContentAbstractionType.ALBUM, "Coke Studio");
                break;
            case "PlayCokeStudioIndiaIntent":
                speechletEventManager.onMusicSelectionIntent(ContentAbstractionType.ALBUM, "Coke Studio @ MTV");
                break;
            case "PlayTheDewaristsIntent":
                speechletEventManager.onMusicSelectionIntent(ContentAbstractionType.ALBUM, "The Dewarists");
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

        logSpeechletResponse(response);

        return response;
    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        logSpeechletRequest("onSessionEnded", requestEnvelope);
    }

    @Override
    public SpeechletResponse onPlaybackFailed(final SpeechletRequestEnvelope<PlaybackFailedRequest> requestEnvelope) {
        logSpeechletRequest("onPlaybackFailed", requestEnvelope);
        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onPlaybackFinished(final SpeechletRequestEnvelope<PlaybackFinishedRequest> requestEnvelope) {
        logSpeechletRequest("onPlaybackFinished", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackNearlyFinished(final SpeechletRequestEnvelope<PlaybackNearlyFinishedRequest> requestEnvelope) {
        logSpeechletRequest("onPlaybackNearlyFinished", requestEnvelope);
        speechletEventManager.onPlaybackNearlyFinished(requestEnvelope.getRequest().getToken());
        return getSpeechletResponse(playbackManager.getNextPlaybackResponse());
    }

    @Override
    public SpeechletResponse onPlaybackStarted(final SpeechletRequestEnvelope<PlaybackStartedRequest> requestEnvelope) {
        logSpeechletRequest("onPlaybackStarted", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackStopped(final SpeechletRequestEnvelope<PlaybackStoppedRequest> requestEnvelope) {
        logSpeechletRequest("onPlaybackStopped", requestEnvelope);
        return null;
    }

    private void logSpeechletRequest(final String tag, final SpeechletRequestEnvelope<?> requestEnvelope) {
        try {
            log.info(tag + " SpeechletRequestEnvelope: " + MAPPER.writeValueAsString(requestEnvelope));
        } catch (final JsonProcessingException e) {
            log.error("Error serializing speechlet request", e);
        }
    }

    private void logSpeechletResponse(final SpeechletResponse response) {
        try {
            log.info("SpeechletResponse: " + MAPPER.writeValueAsString(response));
        } catch (final JsonProcessingException e) {
            log.error("Error serializing speechlet response", e);
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
        SpeechletResponse response = card.isPresent() ? SpeechletResponse.newTellResponse(speech, card.get()) :
                SpeechletResponse.newTellResponse(speech);
        logSpeechletResponse(response);
        return response;
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

    /**
     * Dispatches a progressive response.
     *
     * @param requestId the unique request identifier
     * @param text the speech text
     * @param systemState the SystemState object
     */
    private void dispatchProgressiveResponse(final String requestId, final String text, final SystemState systemState) {
        DirectiveEnvelopeHeader header = DirectiveEnvelopeHeader.builder().withRequestId(requestId).build();
        SpeakDirective directive = SpeakDirective.builder().withSpeech(text).build();
        DirectiveEnvelope directiveEnvelope = DirectiveEnvelope.builder()
                .withHeader(header).withDirective(directive).build();

        if (systemState.getApiAccessToken() != null && !systemState.getApiAccessToken().isEmpty()) {
            String token = systemState.getApiAccessToken();
            try {
                directiveService.enqueue(directiveEnvelope, systemState.getApiEndpoint(), token);
            } catch (final Exception e) {
                log.error("Failed to dispatch a progressive response", e);
            }
        }
    }
}
