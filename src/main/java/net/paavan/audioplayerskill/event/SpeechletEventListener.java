package net.paavan.audioplayerskill.event;

import net.paavan.audioplayerskill.settings.ContentAbstractionType;

public interface SpeechletEventListener {
    void onLaunch();
    void onInitialProgressiveDispatch();

    void onPause();
    void onResume();
    void onCancel();
    void onPrevious();
    void onNext();
    void onRepeat();
    void onStartOver();

    void onPlaybackNearlyFinished(final String token);

    void onLoopOn();
    void onLoopOff();
    void onShuffleOn();
    void onShuffleOff();

    void onMusicSelectionIntent(final ContentAbstractionType contentAbstractionType, final String value);
}
