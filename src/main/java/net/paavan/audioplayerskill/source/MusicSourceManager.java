package net.paavan.audioplayerskill.source;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.event.AbstractSpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MusicSourceManager {
    private final List<MusicSource> musicSources;
    private final S3FileListReader s3FileListReader;
    private boolean isInitialized = false;

    public MusicSourceManager(final S3FileListReader s3FileListReader,
                              final SpeechletEventManager speechletEventManager) {
        this.s3FileListReader = s3FileListReader;
        speechletEventManager.registerSpeechletEventListener(new MusicSourceManagerSpeechletEventListener());
        this.musicSources = new ArrayList<>();
    }

    public void registerMusicSource(final MusicSource... musicSources) {
        this.musicSources.addAll(Arrays.asList(musicSources));
    }

    public List<String> getPlayableAudioUrls() {
        if (!isInitialized) {
            initialize();
        }
        return musicSources.stream()
                .filter(MusicSource::canHandle)
                .map(MusicSource::getAudioUrls)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    // --------------
    // Helper Methods

    /**
     * Reads MP3 keys from S3 and initializes the music sources.
     */
    private void initialize() {
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
            initialize();
        }
    }
}
