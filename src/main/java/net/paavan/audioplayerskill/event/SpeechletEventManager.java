package net.paavan.audioplayerskill.event;

import net.paavan.audioplayerskill.settings.ContentAbstractionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeechletEventManager implements SpeechletEventListener {
    private final List<SpeechletEventListener> speechletEventListeners;

    public SpeechletEventManager() {
        speechletEventListeners = new ArrayList<>();
    }

    public void registerSpeechletEventListener(final SpeechletEventListener... speechletEventListeners) {
        this.speechletEventListeners.addAll(Arrays.asList(speechletEventListeners));
    }

    @Override
    public void onInitialProgressiveDispatch() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onInitialProgressiveDispatch();
        }
    }

    @Override
    public void onLaunch() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onLaunch();
        }
    }

    @Override
    public void onPause() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onPause();
        }
    }

    @Override
    public void onResume() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onResume();
        }
    }

    @Override
    public void onCancel() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onCancel();
        }
    }

    @Override
    public void onPrevious() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onPrevious();
        }
    }

    @Override
    public void onNext() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onNext();
        }
    }

    @Override
    public void onRepeat() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onRepeat();
        }
    }

    @Override
    public void onStartOver() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onStartOver();
        }
    }

    @Override
    public void onPlaybackNearlyFinished(final String token) {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onPlaybackNearlyFinished(token);
        }
    }

    @Override
    public void onLoopOn() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onLoopOn();
        }
    }

    @Override
    public void onLoopOff() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onLoopOff();
        }
    }

    @Override
    public void onShuffleOn() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onShuffleOn();
        }
    }

    @Override
    public void onShuffleOff() {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onShuffleOff();
        }
    }

    @Override
    public void onMusicSelectionIntent(final ContentAbstractionType contentAbstractionType, final String value) {
        for (SpeechletEventListener listener : speechletEventListeners) {
            listener.onMusicSelectionIntent(contentAbstractionType, value);
        }
    }
}
