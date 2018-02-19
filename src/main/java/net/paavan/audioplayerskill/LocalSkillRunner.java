package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletRequestModule;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.speechlet.AudioPlayerSpeechletRequestStreamHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

/**
 * Executes the locally registered Audio Player skill through the {@link AudioPlayerSpeechletRequestStreamHandler} using
 * fabricated requests. While this is useful for local testing only, the code here can be used for integration tests.
 */
@Slf4j
public class LocalSkillRunner {
    private static final String SKILL_ID = "amzn1.ask.skill.22b48f9a-0946-47fb-9a4c-fc731b7a398e";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper() {{
        registerModule(new SpeechletRequestModule());
    }};

    public static void main(String[] args) throws IOException {
        AudioPlayerSpeechletRequestStreamHandler handler = new AudioPlayerSpeechletRequestStreamHandler();

        SpeechletRequestEnvelope<LaunchRequest> launchRequestEnvelope = getLaunchRequestSpeechletRequestEnvelope();
        SpeechletRequestEnvelope<IntentRequest> intentRequestEnvelope = getIntentRequestSpeechletRequestEnvelope();

        InputStream inputStream = new ByteArrayInputStream(OBJECT_MAPPER.writeValueAsBytes(intentRequestEnvelope));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, null);

        log.info(outputStream.toString());
    }

    // --------------
    // Helper Methods

    /**
     * Generates and returns a {@link SpeechletRequestEnvelope<LaunchRequest>} using fabricated data.
     *
     * @return a {@link SpeechletRequestEnvelope<LaunchRequest>} using fabricated data.
     */
    private static SpeechletRequestEnvelope<LaunchRequest> getLaunchRequestSpeechletRequestEnvelope() {
        return SpeechletRequestEnvelope.<LaunchRequest>builder()
                    .withVersion("1.0")
                    .withSession(Session.builder()
                            .withIsNew(true)
                            .withSessionId("someSessionId")
                            .withApplication(new Application(SKILL_ID))
                            .build())
                    .withContext(Context.builder().build())
                    .withRequest(LaunchRequest.builder()
                            .withLocale(Locale.US)
                            .withRequestId("someRequestId")
                            .withTimestamp(new Date())
                            .build())
                    .build();
    }

    /**
     * Generates and returns a {@link SpeechletRequestEnvelope<IntentRequest>} using fabricated data.
     *
     * @return a {@link SpeechletRequestEnvelope<IntentRequest>} using fabricated data.
     */
    private static SpeechletRequestEnvelope<IntentRequest> getIntentRequestSpeechletRequestEnvelope() {
        return SpeechletRequestEnvelope.<IntentRequest>builder()
                .withVersion("1.0")
                .withSession(Session.builder()
                        .withIsNew(true)
                        .withSessionId("someSessionId")
                        .withApplication(new Application(SKILL_ID))
                        .build())
                .withContext(Context.builder().build())
                .withRequest(IntentRequest.builder()
                        .withLocale(Locale.US)
                        .withRequestId("someRequestId")
                        .withTimestamp(new Date())
                        .withIntent(Intent.builder()
                                .withName("PlayTheDewaristsIntent")
                                .build())
                        .build())
                .build();
    }
}
