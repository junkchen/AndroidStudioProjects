package com.junkchen.xfspeech.speech;

public interface ISpeech {
    void speak(String text);
    void stopSpeaking();
    void destroy();
}
