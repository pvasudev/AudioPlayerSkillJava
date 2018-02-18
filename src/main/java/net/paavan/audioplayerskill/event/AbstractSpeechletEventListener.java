package net.paavan.audioplayerskill.event;

import net.paavan.audioplayerskill.settings.ContentAbstractionType;

public abstract class AbstractSpeechletEventListener implements SpeechletEventListener {
    @Override public void onLaunch() {}
    @Override public void onInitialProgressiveDispatch() {}
    @Override public void onPause() {}
    @Override public void onResume() {}
    @Override public void onCancel() {}
    @Override public void onPrevious() {}
    @Override public void onNext() {}
    @Override public void onRepeat() {}
    @Override public void onStartOver() {}
    @Override public void onLoopOn() {}
    @Override public void onLoopOff() {}
    @Override public void onShuffleOn() {}
    @Override public void onShuffleOff() {}
    @Override public void onPlaybackNearlyFinished(final String token) {}
    @Override public void onMusicSelectionIntent(final ContentAbstractionType contentAbstractionType, final String value) {}
}
