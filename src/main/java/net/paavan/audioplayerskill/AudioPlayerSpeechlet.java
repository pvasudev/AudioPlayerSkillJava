package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioItem;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioPlayer;
import com.amazon.speech.speechlet.interfaces.audioplayer.PlayBehavior;
import com.amazon.speech.speechlet.interfaces.audioplayer.Stream;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.StopDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.request.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
public class AudioPlayerSpeechlet implements SpeechletV2, AudioPlayer {
    private static final ObjectMapper MAPPER = new ObjectMapper() {{
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }};

    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        logSpeechletReqeust("onSessionStarted", requestEnvelope);
    }

    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        logSpeechletReqeust("onLaunch", requestEnvelope);
        // TODO: Context is not being passed as expected. File a bug on the team and revisit the previousToken.
        return playMusicFile(getRandomMusicFileToPlay(), requestEnvelope.getSession().isNew(), null);
    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        logSpeechletReqeust("onIntent", requestEnvelope);

        switch(requestEnvelope.getRequest().getIntent().getName()) {
            case "AMAZON.PauseIntent":
                return stopPlayback();
            case "AMAZON.NextIntent":
            default:
                // TODO: Context is not being passed as expected. File a bug on the team and revisit the previousToken.
                return playMusicFile(getRandomMusicFileToPlay(), requestEnvelope.getSession().isNew(), null);
        }
    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        logSpeechletReqeust("onSessionEnded", requestEnvelope);
    }

    @Override
    public SpeechletResponse onPlaybackFailed(final SpeechletRequestEnvelope<PlaybackFailedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackFailed", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackFinished(final SpeechletRequestEnvelope<PlaybackFinishedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackFinished", requestEnvelope);
        return null;
    }

    @Override
    public SpeechletResponse onPlaybackNearlyFinished(final SpeechletRequestEnvelope<PlaybackNearlyFinishedRequest> requestEnvelope) {
        logSpeechletReqeust("onPlaybackNearlyFinished", requestEnvelope);
        return playMusicFile(getRandomMusicFileToPlay(), false, requestEnvelope.getRequest().getToken());
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

    private SpeechletResponse playMusicFile(final String randomFile, final boolean isNewSession, String previousToken) {
        log.info("Playing file: " + randomFile);

        Stream stream = new Stream();
        stream.setUrl(randomFile);
        stream.setToken(randomFile);
        AudioItem audioItem = new AudioItem();
        audioItem.setStream(stream);

        PlayDirective playDirective = new PlayDirective();
        playDirective.setAudioItem(audioItem);
        playDirective.setPlayBehavior(isNewSession ? PlayBehavior.REPLACE_ALL : PlayBehavior.ENQUEUE);
        if (!isNewSession) {
            // TODO: Why is it working when previous token is empty string?
            stream.setExpectedPreviousToken(previousToken);
        }

        SpeechletResponse response = new SpeechletResponse();
        response.setDirectives(Collections.singletonList(playDirective));
        response.setShouldEndSession(true);

        return response;
    }

    private String getRandomMusicFileToPlay() {
        List<String> files = null;
        try {
            files = new PlainTextMultilineFileReader().readFileLinesAsList();
        } catch (IOException e) {
            log.error("Failed to read file", e);
        }

        int randomIndex = new Random().nextInt(files.size());
        return files.get(randomIndex);
    }

    private SpeechletResponse stopPlayback() {
        StopDirective stopDirective = new StopDirective();

        SpeechletResponse response = new SpeechletResponse();
        response.setDirectives(Collections.singletonList(stopDirective));

        return response;
    }

    private void logSpeechletReqeust(final String tag, final SpeechletRequestEnvelope<?> requestEnvelope) {
        try {
            log.info(tag + " SpeechletRequestEnvelope: " +
                    MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(requestEnvelope));
        } catch (JsonProcessingException e) {
            log.error("Error serializing speechlet request", e);
        }
    }
}
