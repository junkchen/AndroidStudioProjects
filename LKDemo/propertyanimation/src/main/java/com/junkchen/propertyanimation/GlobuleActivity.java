package com.junkchen.propertyanimation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

public class GlobuleActivity extends AppCompatActivity {
    public static final String TAG = GlobuleActivity.class.getSimpleName();
    private ImageView globule;
    int screenWidth;
    int screenHeight;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globule);

        globule = (ImageView) findViewById(R.id.globule);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    /**
     * 自由落体运动
     *
     * @param view
     */
    public void verticalMotion(View view) {
        ValueAnimator animator = ValueAnimator
                .ofFloat(0, screenHeight - 300)
                .setDuration(3000);
//        animator.setTarget(globule);
//        animator.setInterpolator(new BounceInterpolator());
//        animator.setInterpolator(new LinearInterpolator());
//        animator.setInterpolator(new AccelerateInterpolator());
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setInterpolator(new CycleInterpolator(2.5f));
        animator.start();
        count = 0;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curHeight = (float) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: curHeight = " + curHeight + ", count = " + (++count));
                globule.setTranslationY(curHeight);
            }
        });
    }

    public void parabolaMotion(View view) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(1000);
        animator.setObjectValues(new PointF(0, 0));
        animator.setInterpolator(new BounceInterpolator());
        animator.setEvaluator(new TypeEvaluator<PointF>() {
            @Override
            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
                Log.i(TAG, "evaluate: fraction = " + fraction + ", count = " + (++count));
                PointF point = new PointF();
                point.x = 200 * fraction * 3;
                point.y = 0.5f * 200 * (fraction * 3) * (fraction * 3);
                return point;
            }
        });
        animator.start();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                globule.setX(point.x);
                globule.setY(point.y);
            }
        });
    }

    public void fadeOutMotion(View view) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(globule, "alpha", 1f, 0.3f)
                .setDuration(1200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i(TAG, "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: ");
                ViewGroup parent = (ViewGroup) globule.getParent();
                if (parent != null) {
                    parent.removeView(globule);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i(TAG, "onAnimationCancel: ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.i(TAG, "onAnimationRepeat: ");
            }
        });
        animator.start();
    }
}
