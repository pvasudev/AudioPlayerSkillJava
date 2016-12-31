package net.paavan.audioplayerskill.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MusicSourceManager {
    private final List<MusicSource> musicSources;

    public MusicSourceManager() {
        this.musicSources = new ArrayList<>();
    }

    public void registerMusicSource(final MusicSource... musicSources) {
        this.musicSources.addAll(Arrays.asList(musicSources));
    }

    public List<String> getPlayableAudioUrls() {
        return musicSources.stream()
                .filter(MusicSource::canHandle)
                .map(MusicSource::getAudioUrls)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
