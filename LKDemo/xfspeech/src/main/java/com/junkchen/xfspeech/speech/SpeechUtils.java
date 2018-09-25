package com.junkchen.xfspeech.speech;

import android.content.Context;

public class SpeechUtils {
    private static ISpeech mSpeech;

    private static final SpeechUtils ourInstance = new SpeechUtils();

    public static SpeechUtils getInstance() {
        return ourInstance;
    }

    public static SpeechUtils initialize(Context context) {
        mSpeech = new XfSpeech(context);
        return ourInstance;
    }

    private SpeechUtils() {

    }

    public static void init(Context context) {
        mSpeech = new XfSpeech(context);
    }

    public static void speak(String text) {
        mSpeech.speak(text);
    }

    public static void stopSpeaking() {
        mSpeech.stopSpeaking();
    }

    public static void destroy() {
        mSpeech.destroy();
    }
}
