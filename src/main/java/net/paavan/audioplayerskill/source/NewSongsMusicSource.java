package net.paavan.audioplayerskill.source;

import net.paavan.audioplayerskill.PlainTextMultilineFileReader;

import java.io.IOException;
import java.util.List;

public class NewSongsMusicSource implements MusicSource {
    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public List<String> getAudioUrls() {
        try {
            return new PlainTextMultilineFileReader().readFileLinesAsList("dropboxNewSongs.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
