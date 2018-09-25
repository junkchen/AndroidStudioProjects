package com.junkchen.autocloseapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Junk on 2017/8/19.
 */

public class BaseActivity extends AppCompatActivity implements Observer {
    public static final String TAG = BaseActivity.class.getSimpleName();
    private CountDownTimer countDownTimer;
    private MyApplication app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.getInstance().addActivity(this);
        ActivityStack.getInstance().pushActivity(this);

        app = (MyApplication) this.getApplication();
        app.getApplicationSubject().attach(this);

        countDownTimer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);
//                close();
//                ActivityStack.getInstance().finishAllActivity();
//                ActivityStack.getInstance().finishAllActivity();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer.start();
        Log.i(TAG, "onResume: ----------stack size: " + ActivityStack.getInstance().size());
        Log.i(TAG, "onResume: ----------ActivityCollector size: " + ActivityCollector.getInstance().size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish: ----------------------finish()------------------");
        ActivityCollector.getInstance().removeActivity(this);
        ActivityStack.getInstance().removeActivity(this);
//        app.getApplicationSubject().detach(this);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "finish: ----------------------onDestroy()------------------");
        app.getApplicationSubject().detach(this);
        super.onDestroy();
    }

    public void close() {
        app.getApplicationSubject().exit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countDownTimer.cancel();
                break;
            case MotionEvent.ACTION_UP:
                countDownTimer.start();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void update(Subject subject) {
        this.finish();
    }
}
