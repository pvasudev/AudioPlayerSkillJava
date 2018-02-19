package net.paavan.audioplayerskill;

import com.amazon.speech.speechlet.interfaces.audioplayer.PlayBehavior;
import lombok.extern.slf4j.Slf4j;
import net.paavan.audioplayerskill.event.AbstractSpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;
import net.paavan.audioplayerskill.settings.ContentAbstractionType;
import net.paavan.audioplayerskill.source.MusicSourceManager;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class PlaybackManagerImpl implements PlaybackManager {
    private final MusicSourceManager musicSourceManager;

    private volatile boolean shouldReturnToken;
    private volatile Optional<String> currentlyPlayingToken = Optional.empty();
    private volatile boolean shouldReplaceCurrentlyPlaying;

    public PlaybackManagerImpl(final SpeechletEventManager speechletEventManager, final MusicSourceManager musicSourceManager) {
        this.musicSourceManager = musicSourceManager;
        speechletEventManager.registerSpeechletEventListener(new PlaybackManagerSpeechletEventListener());
    }

    @Override
    public PlaybackResponse getNextPlaybackResponse() {
        String nextToken = null;
        PlayBehavior playBehavior = null;
        if (shouldReturnToken) {
            nextToken = getRandomAudioUrl(musicSourceManager.getPlayableAudioUrls());
            if (shouldReplaceCurrentlyPlaying) {
                playBehavior = PlayBehavior.REPLACE_ALL;
            } else {
                playBehavior = PlayBehavior.ENQUEUE;
            }
        }

        log.info("Next token to play: " + nextToken);
        return PlaybackResponse.builder()
                .nextToken(Optional.ofNullable(nextToken))
                .expectedToken(playBehavior == PlayBehavior.ENQUEUE ? currentlyPlayingToken : Optional.empty())
                .playBehavior(Optional.ofNullable(playBehavior))
                .build();
    }

    private String getRandomAudioUrl(final List<String> audioUrls) {
        int randomIndex = new Random().nextInt(audioUrls.size());
        return audioUrls.get(randomIndex);
    }

    /**
     * Speechlet Event Listener for Playback Manager.
     */
    private class PlaybackManagerSpeechletEventListener extends AbstractSpeechletEventListener {
        @Override
        public void onPlaybackNearlyFinished(final String token) {
            shouldReturnToken = true;
            currentlyPlayingToken = Optional.of(token);
            shouldReplaceCurrentlyPlaying = false;
        }

        @Override
        public void onLaunch() {
            shouldReturnToken = true;
            currentlyPlayingToken = Optional.empty();
            shouldReplaceCurrentlyPlaying = true;
        }

        @Override
        public void onPause() {
            shouldReturnToken = false;
        }

        @Override
        public void onResume() {
            shouldReturnToken = true;
            shouldReplaceCurrentlyPlaying = false;
        }

        @Override
        public void onNext() {
            shouldReturnToken = true;
            shouldReplaceCurrentlyPlaying = true;
        }

        @Override
        public void onMusicSelectionIntent(final ContentAbstractionType contentAbstractionType, final String value) {
            shouldReturnToken = true;
            shouldReplaceCurrentlyPlaying = true;
        }
    }
}
