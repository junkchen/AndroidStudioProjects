package com.junkchen.propertyanimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by junkchen on 2017/7/13.
 */

public class CircleIndicatorView extends View {
    public static final String TAG = CircleIndicatorView.class.getSimpleName();

    private Paint mBackgroundPaint;
    private Paint mCircleRingPaint;
    private Paint mArcPaint;
    private Paint mCirclePaint;
    private Paint mSmallCirclePaint;
    private Paint mSmallCircleRingPaint;

    private float circleArcSize = 8;
    private float sweepAngle = 90;

    private int smallCircleColor;

    private RectF oval;

    private int degree = 90;

    public CircleIndicatorView(Context context) {
        this(context, null);
    }

    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleIndicatorView, 0, 0);
        try {
            circleArcSize = a.getDimension(R.styleable.CircleIndicatorView_circleArcSize, 8);
            sweepAngle = a.getFloat(R.styleable.CircleIndicatorView_sweepAngle, 90);
            smallCircleColor = a.getColor(R.styleable.CircleIndicatorView_smallCircleColor, 0xFF82C941);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.argb(255, 150, 221, 86));
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mCircleRingPaint = new Paint();
        mCircleRingPaint.setStyle(Paint.Style.STROKE);
        mCircleRingPaint.setAntiAlias(true);
        mCircleRingPaint.setStrokeWidth(8);
        mCircleRingPaint.setColor(Color.argb(66, 255, 255, 255));

        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(circleArcSize);
        mArcPaint.setColor(Color.argb(255, 255, 255, 255));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(10);
        mCirclePaint.setColor(Color.argb(230, 246, 255, 239));

        mSmallCirclePaint = new Paint();
//        mSmallCirclePaint.setColor(Color.argb(0xFF, 0x82, 0xC9, 0x41));
        mSmallCirclePaint.setColor(smallCircleColor);
//        mSmallCirclePaint.setColor(Color.RED);
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);

        mSmallCircleRingPaint = new Paint();
        mSmallCircleRingPaint.setStyle(Paint.Style.STROKE);
        mSmallCircleRingPaint.setAntiAlias(true);
        mSmallCircleRingPaint.setStrokeWidth(8);
        mSmallCircleRingPaint.setColor(Color.argb(255, 255, 255, 255));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        int radius = width / 2 - 20;
//        Log.i(TAG, "onDraw: height = " + height + ", width = " + width);

        //1、画背景
//        canvas.drawRect(0, 0, width, height, mBackgroundPaint);

        //2、画圆环
        canvas.drawCircle(width / 2, height / 2, radius, mCircleRingPaint);

        //3、画实心圆
        canvas.drawCircle(width / 2, height / 2, width / 2 - 40, mCirclePaint);

        //4、画圆弧
        if (oval == null) {
            oval = new RectF(20, 20, width - 20, height - 20);
        }
        canvas.drawArc(oval, -90, sweepAngle, false, mArcPaint);

        //5、画小圆
        double a = (sweepAngle - 90) * Math.PI / 180;
        float cx = (float) (width / 2 + radius * Math.cos(a));
        float cy = (float) (height / 2 + radius * Math.sin(a));
        canvas.drawCircle(cx, cy, 12, mSmallCirclePaint);

        //6、画小圆环
        canvas.drawCircle(cx, cy, 15, mSmallCircleRingPaint);

    }

    public void startAnimation(float sweepAngle) {
        ValueAnimator animator = ValueAnimator
                .ofFloat(0, sweepAngle)
                .setDuration(300);
//        animator.setInterpolator(new BounceInterpolator());
//        animator.setInterpolator(new LinearInterpolator());
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float sweepAngle = (float) animation.getAnimatedValue();
                setSweepAngle(sweepAngle);
            }
        });
    }

    public void startAnimation() {
        ValueAnimator animator = ValueAnimator
                .ofFloat(0, sweepAngle)
                .setDuration(800);
//        animator.setInterpolator(new BounceInterpolator());
//        animator.setInterpolator(new LinearInterpolator());
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float sweepAngle = (float) animation.getAnimatedValue();
                setSweepAngle(sweepAngle);
            }
        });
    }

    public float getCircleArcSize() {
        return circleArcSize;
    }

    public void setCircleArcSize(float circleArcSize) {
        this.circleArcSize = circleArcSize;
//        mArcPaint.setStrokeWidth(circleArcSize);
        invalidate();
        requestLayout();
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidate();
    }

    public int getSmallCircleColor() {
        return smallCircleColor;
    }

    public void setSmallCircleColor(int smallCircleColor) {
        this.smallCircleColor = smallCircleColor;
        mSmallCirclePaint.setColor(smallCircleColor);
        invalidate();
    }
}
