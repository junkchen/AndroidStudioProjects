package com.junkchen.autocloseapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void doClick(View view) {
//        close();
        ActivityStack.getInstance().finishAllActivity();
//        ActivityCollector.getInstance().finishAllActivity();
    }
}
