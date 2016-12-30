package net.paavan.audioplayerskill;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public class AudioPlayerSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>() {{
        add("amzn1.ask.skill.22b48f9a-0946-47fb-9a4c-fc731b7a398e");
    }};

    public AudioPlayerSpeechletRequestStreamHandler() {
        super(new AudioPlayerSpeechlet(), supportedApplicationIds);
    }
}
