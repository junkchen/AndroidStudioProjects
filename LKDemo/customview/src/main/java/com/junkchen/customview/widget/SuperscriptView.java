package com.junkchen.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Junk on 2017/12/13.
 */

public class SuperscriptView extends View {

    private Paint mTextPaint;
    private Paint mBackgroundPaint;

    //Attributes
    private String mText = "角标";
    private float mTextSize = 18;

    public SuperscriptView(Context context) {
        this(context, null);
    }

    public SuperscriptView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperscriptView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(32);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.GREEN);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        int width = getWidth();
//        int height = getHeight();
        int width = getWidth();
        int height = getHeight();

        Path backgroundPath = new Path();
        backgroundPath.moveTo(0, 0);
        backgroundPath.lineTo(width, 0);
        backgroundPath.lineTo(width, height);
        backgroundPath.close();
        canvas.drawPath(backgroundPath, mBackgroundPaint);

        Path textPath = new Path();
        textPath.moveTo(0, 0);
        textPath.lineTo(width, height);
        canvas.drawTextOnPath(mText, textPath, 0, -20 , mTextPaint);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public float getmTextSize() {
        return mTextSize;
    }

    public void setmTextSize(float textSize) {
        this.mTextSize = textSize;
    }
}
