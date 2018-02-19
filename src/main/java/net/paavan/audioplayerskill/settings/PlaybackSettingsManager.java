package net.paavan.audioplayerskill.settings;

import lombok.Getter;
import net.paavan.audioplayerskill.event.AbstractSpeechletEventListener;
import net.paavan.audioplayerskill.event.SpeechletEventManager;

import java.util.Collections;

public class PlaybackSettingsManager {
    private static final PlaybackSettings DEFAULT_PLAYBACK_SETTINGS = PlaybackSettings.builder()
            .loop(false)
            .shuffle(true) // Shuffle is on by default
            .contentRestriction(ContentRestriction.builder()
                    .contentRestrictions(Collections.emptyMap())
                    .build())
            .build();

    @Getter
    private volatile PlaybackSettings playbackSettings;

    public PlaybackSettingsManager(final SpeechletEventManager speechletEventManager) {
        speechletEventManager.registerSpeechletEventListener(new PlaybackSettingsManagerSpeechletEventListener());
        playbackSettings = DEFAULT_PLAYBACK_SETTINGS;
    }

    // --------------
    // Helper Methods

    private class PlaybackSettingsManagerSpeechletEventListener extends AbstractSpeechletEventListener {
        @Override
        public void onRepeat() {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .repeat(true)
                    .build();
        }

//        @Override public void onStartOver() {}

        @Override
        public void onLoopOn() {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .loop(true)
                    .build();
        }

        @Override
        public void onLoopOff() {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .loop(false)
                    .build();
        }

        @Override
        public void onShuffleOn() {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .shuffle(true)
                    .build();
        }

        @Override
        public void onShuffleOff() {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .shuffle(false)
                    .build();
        }

        @Override
        public void onMusicSelectionIntent(final ContentAbstractionType contentAbstractionType, final String value) {
            playbackSettings = playbackSettings.getMutableBuilder()
                    .contentRestriction(ContentRestriction.builder()
                            .contentRestriction(contentAbstractionType, value)
                            .build())
                    .build();
        }
    }
}
