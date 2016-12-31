package net.paavan.audioplayerskill.source;

import java.util.List;

public interface MusicSource {
    boolean canHandle();
    List<String> getAudioUrls();
}
