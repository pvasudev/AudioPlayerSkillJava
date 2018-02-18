package net.paavan.audioplayerskill.source;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MusicSourceManager {
    private final List<MusicSource> musicSources;
    private final S3FileListReader s3FileListReader;

    public MusicSourceManager(final S3FileListReader s3FileListReader) {
        this.s3FileListReader = s3FileListReader;
        this.musicSources = new ArrayList<>();
    }

    public void registerMusicSource(final MusicSource... musicSources) {
        this.musicSources.addAll(Arrays.asList(musicSources));
    }

    /**
     * TODO: Make me event driven.
     */
    public void initialize() {
        ImmutableList<String> s3Keys = ImmutableList.<String>builder()
                .addAll(s3FileListReader.readS3KeysForMp3Files())
                .build();

        musicSources.forEach(musicSource -> musicSource.populateSourceFromS3Keys(s3Keys));
    }

    public List<String> getPlayableAudioUrls() {
        return musicSources.stream()
                .filter(MusicSource::canHandle)
                .map(MusicSource::getAudioUrls)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
