package net.paavan.audioplayerskill;

import com.amazon.speech.speechlet.interfaces.audioplayer.PlayBehavior;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

public interface PlaybackManager {
    @Builder
    @Getter
    class PlaybackResponse {
        private final Optional<String> nextToken;
        private final Optional<String> expectedToken;
        private final Optional<PlayBehavior> playBehavior;
    }

    PlaybackResponse getNextPlaybackResponse();
}
