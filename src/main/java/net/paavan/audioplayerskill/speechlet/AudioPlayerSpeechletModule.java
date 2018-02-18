package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.speechlet.services.DirectiveServiceClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.paavan.audioplayerskill.PlaybackManager;
import net.paavan.audioplayerskill.PlaybackManagerImpl;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.source.AllS3SongsMusicSource;
import net.paavan.audioplayerskill.source.MusicSourceManager;

/**
 * Guice initialization module for all of Audio Player Speechlet.
 */
public class AudioPlayerSpeechletModule extends AbstractModule {
    @Override protected void configure() {}

    @Provides
    @Singleton
    SpeechletEventManager getSpeechletEventManager() {
        return new SpeechletEventManager();
    }

    @Provides
    @Singleton
    MusicSourceManager getMusicSourceManager() {
        MusicSourceManager musicSourceManager = new MusicSourceManager();
        musicSourceManager.registerMusicSource(new AllS3SongsMusicSource());
        return musicSourceManager;
    }

    @Provides
    @Singleton
    PlaybackManager getPlaybackManager(final SpeechletEventManager speechletEventManager,
                                       final MusicSourceManager musicSourceManager) {
        return new PlaybackManagerImpl(speechletEventManager, musicSourceManager);
    }

    @Provides
    @Singleton
    DirectiveServiceClient getDirectiveServiceClient() {
        return new DirectiveServiceClient();
    }

    @Provides
    @Singleton
    AudioPlayerSpeechlet getAudioPlayerSpeechlet(final PlaybackManager playbackManager,
                                                 final SpeechletEventManager speechletEventManager,
                                                 final DirectiveServiceClient directiveServiceClient) {
        return new AudioPlayerSpeechlet(playbackManager, speechletEventManager, directiveServiceClient);
    }
}
