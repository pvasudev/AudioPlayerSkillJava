package net.paavan.audioplayerskill.settings;

import lombok.Builder;
import lombok.Getter;

/**
 * An immutable object representing a snapshot of collated playback settings that have been collected from user inputs
 * over many voice requests.
 */
@Getter
@Builder
public class PlaybackSettings {
    private final boolean shuffle;
    private final boolean loop;
    private final boolean repeat;
    private final ContentRestriction contentRestriction;

    /**
     * Creates and returns a mutable builder that has already been initialized with the values read from this
     * {@link PlaybackSettings} instance.
     *
     * @return a mutable builder that has already been initialized with the values read from this instance
     */
    public PlaybackSettingsBuilder getMutableBuilder() {
        return PlaybackSettings.builder()
                .loop(this.loop)
                .shuffle(this.shuffle)
                .repeat(this.repeat)
                .contentRestriction(this.contentRestriction);
    }
}
