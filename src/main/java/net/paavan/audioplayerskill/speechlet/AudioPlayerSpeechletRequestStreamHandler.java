package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import net.paavan.audioplayerskill.PlaybackManager;
import net.paavan.audioplayerskill.PlaybackManagerImpl;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.source.AllS3SongsMusicSource;
import net.paavan.audioplayerskill.source.MusicSourceManager;

import java.util.HashSet;
import java.util.Set;

public class AudioPlayerSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> SUPPORTED_APPLICATION_IDS = new HashSet<String>() {{
        add("amzn1.ask.skill.22b48f9a-0946-47fb-9a4c-fc731b7a398e");
    }};

    private static final MusicSourceManager MUSIC_SOURCE_MANAGER = new MusicSourceManager() {{
        registerMusicSource(new AllS3SongsMusicSource());
    }};

    private static final SpeechletEventManager SPEECHLET_EVENT_MANAGER = new SpeechletEventManager();
    private static final PlaybackManager PLAYBACK_MANAGER = new PlaybackManagerImpl(SPEECHLET_EVENT_MANAGER, MUSIC_SOURCE_MANAGER);

    public AudioPlayerSpeechletRequestStreamHandler() {
        super(new AudioPlayerSpeechlet(PLAYBACK_MANAGER, SPEECHLET_EVENT_MANAGER), SUPPORTED_APPLICATION_IDS);
    }
}
