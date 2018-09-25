package com.junkchen.propertyanimation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_globule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GlobuleActivity.class));
            }
        });
        findViewById(R.id.btn_motion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CircleIndicatorActivity.class));
            }
        });
    }

    public void doAnimation(final View view) {
//        ObjectAnimator.ofFloat(view, "rotationX", 0.0f, 360f)
//                .setDuration(800)
//                .start();

        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, "junkchen", 1.0f, 0.3f)
                .setDuration(800);
        animator.start();
        count = 0;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: value = " + value + ", count = " + (++count));
                view.setAlpha(value);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
    }
}
