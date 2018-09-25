package com.junkchen.xfspeech;

import android.app.Application;

import com.junkchen.xfspeech.speech.SpeechUtils;

public class SpeechApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        SpeechUtils.getInstance().init(this);
//        SpeechUtils.init(this);
        SpeechUtils.initialize(this);
    }
}
