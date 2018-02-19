package net.paavan.audioplayerskill.speechlet;

import com.amazon.speech.speechlet.services.DirectiveServiceClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.paavan.audioplayerskill.PlaybackManager;
import net.paavan.audioplayerskill.PlaybackManagerImpl;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.settings.PlaybackSettingsManager;
import net.paavan.audioplayerskill.settings.PlaybackSettingsManagerImpl;
import net.paavan.audioplayerskill.source.CokeStudioIndiaMusicSource;
import net.paavan.audioplayerskill.source.CokeStudioMusicSource;
import net.paavan.audioplayerskill.source.MusicSourceManager;
import net.paavan.audioplayerskill.source.S3FileListReader;
import net.paavan.audioplayerskill.source.TheDewaristsMusicSource;

/**
 * Guice initialization module for all of Audio Player Speechlet.
 */
public class AudioPlayerSpeechletModule extends AbstractModule {
    private static final String S3_BUCKET_NAME = "audioplayerskill";
    private static final String S3_SONGS_DIRECTORY_PREFIX = "Songs";

    @Override protected void configure() {}

    @Provides
    @Singleton
    SpeechletEventManager getSpeechletEventManager() {
        return new SpeechletEventManager();
    }

    @Provides
    @Singleton
    S3FileListReader getS3FileListReader() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        return new S3FileListReader(s3Client, S3_BUCKET_NAME, S3_SONGS_DIRECTORY_PREFIX);
    }

    @Provides
    @Singleton
    PlaybackSettingsManager getPlaybackSettingsManager(final SpeechletEventManager speechletEventManager) {
        return new PlaybackSettingsManagerImpl(speechletEventManager);
    }

    @Provides
    @Singleton
    MusicSourceManager getMusicSourceManager(final S3FileListReader s3FileListReader,
                                             final SpeechletEventManager speechletEventManager,
                                             final PlaybackSettingsManager playbackSettingsManager) {
        MusicSourceManager musicSourceManager = new MusicSourceManager(s3FileListReader, speechletEventManager,
                playbackSettingsManager);
        musicSourceManager.registerMusicSource(new CokeStudioMusicSource(), new CokeStudioIndiaMusicSource(),
                new TheDewaristsMusicSource());
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
