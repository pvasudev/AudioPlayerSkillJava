package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class MusicSelectionIntentRequestIntegrationTest extends IntegrationTestBase {
    @Test
    public void playCokeStudioIntentRequest() throws IOException {
        SpeechletRequestEnvelope<IntentRequest> intentRequestEnvelope = getIntentRequestSpeechletRequestEnvelope(
                "PlayCokeStudioIntent");
        invokeSkill(intentRequestEnvelope);
    }

    @Test
    public void playCokeStudioIndiaIntentRequest() throws IOException {
        SpeechletRequestEnvelope<IntentRequest> intentRequestEnvelope = getIntentRequestSpeechletRequestEnvelope(
                "PlayCokeStudioIndiaIntent");
        invokeSkill(intentRequestEnvelope);
    }

    @Test
    public void playDewaristsIntentRequest() throws IOException {
        SpeechletRequestEnvelope<IntentRequest> intentRequestEnvelope = getIntentRequestSpeechletRequestEnvelope(
                "PlayTheDewaristsIntent");
        invokeSkill(intentRequestEnvelope);
    }

    // --------------
    // Helper Methods

    /**
     * Generates and returns a {@link SpeechletRequestEnvelope<IntentRequest>} using fabricated data.
     *
     * @return a {@link SpeechletRequestEnvelope<IntentRequest>} using fabricated data.
     * @param intentName
     */
    private static SpeechletRequestEnvelope<IntentRequest> getIntentRequestSpeechletRequestEnvelope(
            final String intentName) {
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
                                .withName(intentName)
                                .build())
                        .build())
                .build();
    }
}
