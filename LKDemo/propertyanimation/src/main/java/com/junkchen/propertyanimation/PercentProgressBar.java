package com.junkchen.propertyanimation;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by junkchen on 2017/7/15.
 */

public class PercentProgressBar extends View {
    public static final String TAG = PercentProgressBar.class.getSimpleName();

    private Paint backgroundPaint;//背景
    private Paint foregroundPaint;//前景
    private Paint textPaint;//文字

    private int percent = 50;//百分比的比值

    public PercentProgressBar(Context context) {
        this(context, null);
    }

    public PercentProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PercentProgressBar, 0, 0);
        try {
            percent = typedArray.getInteger(R.styleable.PercentProgressBar_percent, 100);
        } finally {
            typedArray.recycle();
        }

        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setStrokeWidth(50);
        backgroundPaint.setAntiAlias(true);

        foregroundPaint = new Paint();
        foregroundPaint.setColor(Color.rgb(0x73, 0xC1, 0x2F));
        foregroundPaint.setStyle(Paint.Style.FILL);
        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);
        foregroundPaint.setStrokeWidth(50);
        foregroundPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
//        textPaint.setTextSize(30);
//        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();

        RectF rect = new RectF(0, 0, width, height);

        canvas.drawRoundRect(rect, height / 2, height / 2, backgroundPaint);

        float right = width * percent / 100f;
        rect.right = right;
        canvas.drawRoundRect(rect, height / 2, height / 2, foregroundPaint);

        textPaint.setTextSize(height / 10 * 8);
//        canvas.drawText(percent + "%", width / 2 - 60, height - 10, textPaint);

        String percentStr = percent + "%";
        Rect bounds = new Rect();
        textPaint.getTextBounds(percentStr, 0, percentStr.length(), bounds);
//        Log.i(TAG, "onDraw: width = " + width + ", height = " + height);
//        Log.i(TAG, "onDraw: text size: " + textPaint.getTextSize());
//        Log.i(TAG, "onDraw: left = " + bounds.left + ", right = " + bounds.right +
//                ", top = " + bounds.top + ", bottom = " + bounds.bottom);
        float x = right - (bounds.right - bounds.left) - height / 3;
        float y = height - (height - (bounds.bottom - bounds.top)) / 2f;
        canvas.drawText(percentStr, x, y, textPaint);
    }

    public void startAnimation() {
        ValueAnimator animator = ValueAnimator
                .ofInt(0, percent)
                .setDuration(1*1000);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.setInterpolator(new AccelerateInterpolator());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int tmpPercent = (int) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: tmpPercent = " + tmpPercent +
                ", fraction = " + animation.getAnimatedFraction());
                setPercent(tmpPercent);
            }
        });
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
//        if (percent < 0 || percent > 100)
//            throw new RuntimeException("percent can't less than 0 and more than 100");
        if (percent < 0) this.percent = 0;
        else if (percent > 100) this.percent = 100;
        else this.percent = percent;
        invalidate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    class custom extends BaseInterpolator {

        @Override
        public float getInterpolation(float input) {
            return 0;
        }
    }
}
