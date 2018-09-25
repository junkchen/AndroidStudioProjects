package com.junkchen.propertyanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class CircleIndicatorActivity extends AppCompatActivity {
    CircleIndicatorView circleIndicator;
    PercentProgressBar percentBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_indicator);
        circleIndicator = (CircleIndicatorView) findViewById(R.id.circleIndicator);
        percentBar = (PercentProgressBar) findViewById(R.id.percentBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        circleIndicator.startAnimation();
        percentBar.startAnimation();
    }

    public void startAnimation(View view) {
        ((CircleIndicatorView)view).startAnimation(new Random().nextInt(360));
        percentBar.setPercent(new Random().nextInt(100));
        percentBar.startAnimation();
    }
}
