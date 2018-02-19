package net.paavan.audioplayerskill.settings;

/**
 * A Playback Settings manager is the repository for the most up-to-date copy of the {@link PlaybackSettings} object.
 * The Playback Settings manager listens to various speechlet events and updates the {@link PlaybackSettings} object.
 */
public interface PlaybackSettingsManager {
    /**
     * Returns the current {@link PlaybackSettings} object. The object may get stale either due to occurrence of a new
     * speechlet event or due to other temporal and skill lifecycle events. The returned object is immutable and will
     * not be automatically updated. The callers are encouraged to call this method to get the most current copy of the
     * PlaybackSettings.
     *
     * @return the current {@link PlaybackSettings} object
     */
    PlaybackSettings getPlaybackSettings();
}
