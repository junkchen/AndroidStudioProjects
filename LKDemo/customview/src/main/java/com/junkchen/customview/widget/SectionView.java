package com.junkchen.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Junk on 2017/11/3.
 */

public class SectionView extends View {
    //Paint
    private Paint mIndicatorPaint;
    private Paint mSectionPaint;
    private Paint mTextPaint;

    //Attributes
    private int mSectionAmount = 3;
    private int indicatorColor;
    private int[] mSectionColors = new int[]{Color.RED, Color.GREEN, Color.BLUE};
    private int textColor = Color.BLACK;
    private String[] mSectionTexts = new String[]{"Google", "ABC123xyz", "Apple"};
    private float textSize = 30;
    private int indicatorLocation = 1;

    //Member filed
    private Paint.FontMetrics mFontMetrics;
    private float indicatorWidth = 18;//指示器宽度
    private float indicatorHeight = 30;//指示器高度
    private float sectionHeight = 36;//颜色快高度
    private float indicatorAndSectionInterval = 8;//指示器和颜色块之间的间隔距离
    private float sectionAndTextInterval = 4;//指示器和颜色块之间的间隔距离

    public SectionView(Context context) {
        this(context, null);
    }

    public SectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(indicatorColor);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);

        mSectionPaint = new Paint();
        mSectionPaint.setColor(mSectionColors[0]);
        mSectionPaint.setStyle(Paint.Style.FILL);
        mSectionPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mFontMetrics = mTextPaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 480;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {

        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mTextPaint.setTextSize(textSize);
            height = (int) (getPaddingTop() + indicatorHeight + indicatorAndSectionInterval
                    + sectionHeight + sectionAndTextInterval + mFontMetrics.bottom - mFontMetrics.top + getPaddingBottom());
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1. Draw top indicator
        drawIndicator(canvas);
        //2. Draw three section
        drawSection(canvas);
        //3. Draw text
        drawText(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        Path indicatorPath = new Path();

        float indicatorHalfWidth = indicatorWidth / 2;
        float indicatorHalfHeight = indicatorHeight / 2;

        //Path 路线从左往右进行闭合
        // left_top -> right_top -> right_center -> _bottom_center -> left_center
        indicatorPath.moveTo(getWidth() / (mSectionAmount * 2) * (indicatorLocation * 2 + 1) - indicatorHalfWidth, getPaddingTop());
        indicatorPath.lineTo(getWidth() / (mSectionAmount * 2) * (indicatorLocation * 2 + 1) + indicatorHalfWidth, getPaddingTop());
        indicatorPath.lineTo(getWidth() / (mSectionAmount * 2) * (indicatorLocation * 2 + 1) + indicatorHalfWidth, getPaddingTop() + indicatorHalfHeight);
        indicatorPath.lineTo(getWidth() / (mSectionAmount * 2) * (indicatorLocation * 2 + 1), getPaddingTop() + indicatorHeight);
        indicatorPath.lineTo(getWidth() / (mSectionAmount * 2) * (indicatorLocation * 2 + 1) - indicatorHalfWidth, getPaddingTop() + indicatorHalfHeight);
        indicatorPath.close();
        mIndicatorPaint.setColor(mSectionColors[indicatorLocation]);
        canvas.drawPath(indicatorPath, mIndicatorPaint);
    }

    private void drawSection(Canvas canvas) {
        float sectionTop = getPaddingTop() + indicatorHeight + indicatorAndSectionInterval;
        float sectionBottom = sectionTop + sectionHeight;
        for (int i = 0; i < mSectionAmount; i++) {
            mSectionPaint.setColor(mSectionColors[i]);
            canvas.drawRect(getWidth() / mSectionAmount * i, sectionTop, getWidth(), sectionBottom, mSectionPaint);
        }
    }

    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(textSize);
        float textHeight = getHeight() - (getPaddingTop() + indicatorHeight + indicatorAndSectionInterval +
                sectionHeight + sectionAndTextInterval + getPaddingBottom());
//        float y = getPaddingTop() + indicatorHeight + indicatorAndSectionInterval +
//                sectionHeight + sectionAndTextInterval + textHeight / 2 + (mFontMetrics.bottom - mFontMetrics.top) / 2 - mFontMetrics.bottom;
        float y = getPaddingTop() + indicatorHeight + indicatorAndSectionInterval +
                sectionHeight + sectionAndTextInterval + textHeight / 2 - (mFontMetrics.top + mFontMetrics.bottom) / 2;

        for (int i = 0; i < mSectionAmount; i++) {
            canvas.drawText(mSectionTexts[i], getWidth() / (mSectionAmount * 2) * (2 * i + 1), y, mTextPaint);
        }
    }

    public void setSectionConfig(int sectionAmount, @ColorInt int[] sectionColors,
                                 String[] sectionTexts, int indicatorLocation) {
        this.mSectionAmount = sectionAmount;
        this.mSectionColors = sectionColors;
        this.mSectionTexts = sectionTexts;
        this.indicatorLocation = indicatorLocation;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
