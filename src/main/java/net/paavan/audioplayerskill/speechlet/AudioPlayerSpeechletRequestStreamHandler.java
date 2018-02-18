package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.HashSet;
import java.util.Set;

public class AudioPlayerSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> SUPPORTED_APPLICATION_IDS = new HashSet<String>() {{
        add("amzn1.ask.skill.22b48f9a-0946-47fb-9a4c-fc731b7a398e");
    }};

    public static final Injector INJECTOR = Guice.createInjector(new AudioPlayerSpeechletModule());

    public AudioPlayerSpeechletRequestStreamHandler() {
        super(INJECTOR.getInstance(AudioPlayerSpeechlet.class), SUPPORTED_APPLICATION_IDS);
    }
}
