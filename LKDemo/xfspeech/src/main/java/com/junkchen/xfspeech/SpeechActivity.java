package com.junkchen.xfspeech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.junkchen.xfspeech.speech.SpeechUtils;

public class SpeechActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
//        initSpeech();
        findViewById(R.id.btn_speak).setOnClickListener(v -> speak());

        findViewById(R.id.btn_stop).setOnClickListener(v -> SpeechUtils.stopSpeaking());
    }

    private void initSpeech() {
        SpeechUtils.getInstance().init(this);
    }

    private void speak() {
        String text = "科大讯飞，让世界聆听我们的声音。科大讯飞，让世界聆听我们的声音。科大讯飞，让世界聆听我们的声音。";
//        SpeechUtils.getInstance().speak(text);
        SpeechUtils.speak(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SpeechUtils.getInstance().destroy();
        SpeechUtils.destroy();
    }
}
