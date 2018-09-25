package com.junkchen.autocloseapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

//    private CountDownTimer countDownTimer;

    private TextView txtv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtv_time = (TextView) findViewById(R.id.txtv_time);

//        final RelativeLayout rltv_content = (RelativeLayout) findViewById(R.id.rltv_content);
//        Log.i("JunkChen", "onCreate: rltv_content.getHeight() = " + rltv_content.getHeight());
//        final RelativeLayout record_overview_server_profile_container = (RelativeLayout) findViewById(R.id.record_overview_server_profile_container);
//        Log.i("JunkChen", "onCreate: record_overview_server_profile_container.getHeight() = " + record_overview_server_profile_container.getHeight());
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("JunkChen", "onCreate: rltv_content.getHeight() = " + rltv_content.getHeight());
//                Log.i("JunkChen", "onCreate: record_overview_server_profile_container.getHeight() = " + record_overview_server_profile_container.getHeight());
//            }
//        }, 3000);

//        countDownTimer = new CountDownTimer(15*1000, 3000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                txtv_time.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            @Override
//            public void onFinish() {
//                txtv_time.setText("done!");
//            }
//        };
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        countDownTimer.start();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                countDownTimer.cancel();
//                break;
//            case MotionEvent.ACTION_UP:
//                countDownTimer.start();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    public void doClick(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}
