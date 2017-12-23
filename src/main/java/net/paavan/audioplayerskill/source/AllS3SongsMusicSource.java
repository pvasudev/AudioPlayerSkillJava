package net.paavan.audioplayerskill.source;

import net.paavan.audioplayerskill.PlainTextMultilineFileReader;

import java.io.IOException;
import java.util.List;

public class AllS3SongsMusicSource implements MusicSource {
    private static final String S3_SONGS_URLS = "S3Songs.txt";

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public List<String> getAudioUrls() {
        try {
            return new PlainTextMultilineFileReader().readFileLinesAsList(S3_SONGS_URLS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
