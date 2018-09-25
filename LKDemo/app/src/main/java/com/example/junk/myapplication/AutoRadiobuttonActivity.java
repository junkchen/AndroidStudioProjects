package com.example.junk.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AutoRadiobuttonActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_radiobutton);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);

        RadioButton radioButton = new RadioButton(this);
        radioButton.setWidth(0);
        radioButton.setText("脂肪");
        radioButton.setGravity(View.TEXT_ALIGNMENT_CENTER);

        mRadioGroup.addView(radioButton);
    }
}
