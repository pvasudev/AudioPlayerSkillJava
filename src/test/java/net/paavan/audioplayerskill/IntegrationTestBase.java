package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletRequestModule;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.speechlet.AudioPlayerSpeechletRequestStreamHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class IntegrationTestBase {
    public static final String SKILL_ID = "amzn1.ask.skill.22b48f9a-0946-47fb-9a4c-fc731b7a398e";

    private AudioPlayerSpeechletRequestStreamHandler handler = new AudioPlayerSpeechletRequestStreamHandler();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper() {{
        registerModule(new SpeechletRequestModule());
    }};

    /* package-private */ void invokeSkill(final SpeechletRequestEnvelope<? extends SpeechletRequest> requestEnvelope)
            throws IOException {
        InputStream inputStream = new ByteArrayInputStream(OBJECT_MAPPER.writeValueAsBytes(requestEnvelope));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, null);

        log.info(outputStream.toString());
    }
}
