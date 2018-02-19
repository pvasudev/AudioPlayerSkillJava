package net.paavan.audioplayerskill.source;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.event.AbstractSpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.settings.PlaybackSettings;
import net.paavan.audioplayerskill.settings.PlaybackSettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MusicSourceManager {
    private final S3FileListReader s3FileListReader;
    private final PlaybackSettingsManager playbackSettingsManager;

    private final List<MusicSource> musicSources;
    private boolean isInitialized = false;

    public MusicSourceManager(final S3FileListReader s3FileListReader,
                              final SpeechletEventManager speechletEventManager,
                              final PlaybackSettingsManager playbackSettingsManager) {
        this.s3FileListReader = s3FileListReader;
        this.playbackSettingsManager = playbackSettingsManager;
        speechletEventManager.registerSpeechletEventListener(new MusicSourceManagerSpeechletEventListener());

        this.musicSources = new ArrayList<>();
    }

    public void registerMusicSource(final MusicSource... musicSources) {
        this.musicSources.addAll(Arrays.asList(musicSources));
    }

    public List<String> getPlayableAudioUrls() {
        initializeIfNeeded();
        PlaybackSettings playbackSettings = playbackSettingsManager.getPlaybackSettings();

        return musicSources.stream()
                .filter(musicSource -> musicSource.canHandle(playbackSettings))
                .map(MusicSource::getAudioUrls)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    // --------------
    // Helper Methods

    /**
     * Reads MP3 keys from S3 and initializes the music sources, if it isn't already initialized.
     */
    private void initializeIfNeeded() {
        if (isInitialized) {
            log.info("Music Source is already initialized. Returning.");
            return;
        }

        ImmutableList<String> s3Keys = ImmutableList.<String>builder()
                .addAll(s3FileListReader.readS3KeysForMp3Files())
                .build();

        musicSources.forEach(musicSource -> musicSource.populateSourceFromS3Keys(s3Keys));
        isInitialized = true;
    }

    /**
     * Speehlet event listener for the MusicSourceManager
     */
    private class MusicSourceManagerSpeechletEventListener extends AbstractSpeechletEventListener {
        @Override
        public void onInitialProgressiveDispatch() {
            initializeIfNeeded();
        }
    }
}
