package net.paavan.audioplayerskill;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class LaunchRequestIntegrationTest extends IntegrationTestBase {
    @Test
    public void invokeSkillWithLaunchRequest() throws IOException {
        SpeechletRequestEnvelope<LaunchRequest> launchRequestEnvelope = getLaunchRequestSpeechletRequestEnvelope();
        invokeSkill(launchRequestEnvelope);
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
}
