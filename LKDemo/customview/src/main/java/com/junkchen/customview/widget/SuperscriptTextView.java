package com.junkchen.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Junk on 2017/12/14.
 */

public class SuperscriptTextView extends TextView {
    private Paint mTextPaint;
    private Paint mBackgroundPaint;

    //Attributes
    private String mSuperscriptText = "角标";
    private float mSuperscriptTextSize = 18;

    public SuperscriptTextView(Context context) {
        super(context);
        init();
    }

    public SuperscriptTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuperscriptTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
//        mBackgroundPaint.setColor(Color.GREEN);
        mBackgroundPaint.setColor(Color.parseColor("#00C853"));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        Path backgroundPath = new Path();
        backgroundPath.moveTo(width - 136, 14);
        backgroundPath.lineTo(width-20, 14);
        backgroundPath.lineTo(width-20, 136);
        backgroundPath.close();
        canvas.drawPath(backgroundPath, mBackgroundPaint);

        Path textPath = new Path();
        textPath.moveTo(width - 136, 14);
        textPath.lineTo(width-20, 136);
        canvas.drawTextOnPath("已检测", textPath, 0, -8, mTextPaint);
    }

}
