package net.paavan.audioplayerskill.source;

import net.paavan.audioplayerskill.settings.PlaybackSettings;

import java.util.List;

public interface MusicSource {
    void populateSourceFromS3Keys(final List<String> s3Keys);
    boolean canHandle(final PlaybackSettings playbackSettings);
    List<String> getAudioUrls();
}
