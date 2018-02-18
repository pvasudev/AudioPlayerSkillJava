package net.paavan.audioplayerskill.source;

import java.util.List;

public interface MusicSource {
    void populateSourceFromS3Keys(final List<String> s3Keys);
    boolean canHandle();
    List<String> getAudioUrls();
}
