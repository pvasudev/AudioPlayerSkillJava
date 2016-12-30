package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioItem;
import com.amazon.speech.speechlet.interfaces.audioplayer.PlayBehavior;
import com.amazon.speech.speechlet.interfaces.audioplayer.Stream;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class AudioPlayerSpeechlet implements SpeechletV2 {
    private static final ObjectMapper MAPPER = new ObjectMapper() {{
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }};

    public static void main(String[] args) throws IOException {
        System.out.println("Test");
        System.out.println(new FileReaderD().readFile());
    }

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        logSpeechletReqeust(requestEnvelope);
        log.debug("onSessionStarted");
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        logSpeechletReqeust(requestEnvelope);
        return playRandomMusicFromFile();
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        logSpeechletReqeust(requestEnvelope);
        return playRandomMusicFromFile();
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        logSpeechletReqeust(requestEnvelope);
    }

    private SpeechletResponse playRandomMusicFromFile() {
        List<String> files = null;
        try {
            files = new FileReaderD().readFile();
        } catch (IOException e) {
            log.error("Failed to read file", e);
        }

        int randomIndex = new Random().nextInt(files.size());
        Stream stream = new Stream();
        String randomFile = files.get(randomIndex);
        log.info("Playing file: " + randomFile);
        stream.setUrl(randomFile);
        stream.setToken(randomFile);
        AudioItem audioItem = new AudioItem();
        audioItem.setStream(stream);

        PlayDirective playDirective = new PlayDirective();
        playDirective.setAudioItem(audioItem);
        playDirective.setPlayBehavior(PlayBehavior.REPLACE_ALL);

        SpeechletResponse response = new SpeechletResponse();
        response.setDirectives(Arrays.asList(playDirective));

        return response;
    }

    private void logSpeechletReqeust(final SpeechletRequestEnvelope<?> requestEnvelope) {
        try {
            log.info("SpeechletRequestEnvelope: " +
                    MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(requestEnvelope));
        } catch (JsonProcessingException e) {
            log.error("Error serializing speechlet requset", e);
        }
    }
}
