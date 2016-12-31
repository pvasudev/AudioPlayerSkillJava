package net.paavan.audioplayerskill.event;

public interface SpeechletEventListener {
    void onLaunch();

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
}
