package com.junkchen.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.junkchen.customview.R;

/**
 * Created by junkchen on 2017/8/6.
 */

public class ThreeSectionView extends View {
    private static final String TAG = ThreeSectionView.class.getSimpleName();

    //Paint
    private Paint mIndicatorPaint;
    private Paint mSectionPaint;
    private Paint mTextPaint;

    //Attributes
    private int indicatorColor;
    private int leftSectionColor;
    private int centerSectionColor;
    private int rightSectionColor;
    private int leftTextColor;
    private int centerTextColor;
    private int rightTextColor;
    private String leftText = "L";
    private String centerText = "C";
    private String rightText = "R";
    private int textColor;
    private float textSize;
    private int indicatorLocation;

    //Member filed
    private float indicatorWidth = 18;//指示器宽度
    private float indicatorHeight = 30;//指示器高度
    private float sectionHeight = 36;//颜色快高度
    private float indicatorAndSectionInterval = 8;//指示器和颜色块之间的间隔距离
    private float sectionAndTextInterval = 8;//指示器和颜色块之间的间隔距离

    public ThreeSectionView(Context context) {
        this(context, null);
    }

    public ThreeSectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.ThreeSectionView, 0, 0);
        try {
            indicatorColor = attributes.getColor(R.styleable.ThreeSectionView_indicatorColor, Color.GREEN);
            leftSectionColor = attributes.getColor(R.styleable.ThreeSectionView_leftSectionColor, Color.RED);
            centerSectionColor = attributes.getColor(R.styleable.ThreeSectionView_centerSectionColor, Color.GREEN);
            rightSectionColor = attributes.getColor(R.styleable.ThreeSectionView_rightSectionColor, Color.BLUE);
            leftTextColor = attributes.getColor(R.styleable.ThreeSectionView_leftTextColor, Color.RED);
            centerTextColor = attributes.getColor(R.styleable.ThreeSectionView_centerTextColor, Color.BLUE);
            rightTextColor = attributes.getColor(R.styleable.ThreeSectionView_rightTextColor, Color.GREEN);
            leftText = attributes.getString(R.styleable.ThreeSectionView_leftText);
            centerText = attributes.getString(R.styleable.ThreeSectionView_centerText);
            rightText = attributes.getString(R.styleable.ThreeSectionView_rightText);
            textColor = attributes.getColor(R.styleable.ThreeSectionView_textColor, Color.BLACK);
            textSize = attributes.getDimension(R.styleable.ThreeSectionView_textSize, 16);
            indicatorLocation = attributes.getInt(R.styleable.ThreeSectionView_indicatorLocation, 0);
        } finally {
            attributes.recycle();
        }

        init();
    }


    private void init() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(indicatorColor);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);

        mSectionPaint = new Paint();
        mSectionPaint.setColor(leftSectionColor);
        mSectionPaint.setStyle(Paint.Style.FILL);
        mSectionPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
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
            Rect bounds = new Rect();
            mTextPaint.setTextSize(textSize);
            mTextPaint.getTextBounds(leftText, 0, leftText.length(), bounds);
            height = (int) (getPaddingTop() + indicatorHeight + indicatorAndSectionInterval
                    + sectionHeight + sectionAndTextInterval + bounds.height() + 8 + getPaddingBottom());
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
        if (indicatorLocation == 0) {
            indicatorPath.moveTo(getWidth() / 6 - indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 + indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 + indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.lineTo(getWidth() / 6, indicatorHeight);
            indicatorPath.lineTo(getWidth() / 6 - indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.close();
            mIndicatorPaint.setColor(leftSectionColor);
        } else if (indicatorLocation == 1) {
            indicatorPath.moveTo(getWidth() / 6 * 3 - indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 * 3 + indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 * 3 + indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.lineTo(getWidth() / 6 * 3, indicatorHeight);
            indicatorPath.lineTo(getWidth() / 6 * 3 - indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.close();
            mIndicatorPaint.setColor(centerSectionColor);
        } else if (indicatorLocation == 2) {
            indicatorPath.moveTo(getWidth() / 6 * 5 - indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 * 5 + indicatorHalfWidth, 0);
            indicatorPath.lineTo(getWidth() / 6 * 5 + indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.lineTo(getWidth() / 6 * 5, indicatorHeight);
            indicatorPath.lineTo(getWidth() / 6 * 5 - indicatorHalfWidth, indicatorHalfHeight);
            indicatorPath.close();
            mIndicatorPaint.setColor(rightSectionColor);
        }
        canvas.drawPath(indicatorPath, mIndicatorPaint);

        //        Path leftIndicatorPath = new Path();
//        Path centerIndicatorPath = new Path();
//        Path rightIndicatorPath = new Path();

//        //left indicator
//        leftIndicatorPath.moveTo(getWidth() / 6 - indicatorHalfWidth, 0);
//        leftIndicatorPath.lineTo(getWidth() / 6 + indicatorHalfWidth, 0);
//        leftIndicatorPath.lineTo(getWidth() / 6 + indicatorHalfWidth, getHeight() / 12 * 2);
//        leftIndicatorPath.lineTo(getWidth() / 6, getHeight() / 3 - 2);
//        leftIndicatorPath.lineTo(getWidth() / 6 - indicatorHalfWidth, getHeight() / 12 * 2);
//        leftIndicatorPath.close();
//
//        //center indicator
//        centerIndicatorPath.moveTo(getWidth() / 6 * 3 - indicatorHalfWidth, 0);
//        centerIndicatorPath.lineTo(getWidth() / 6 * 3 + indicatorHalfWidth, 0);
//        centerIndicatorPath.lineTo(getWidth() / 6 * 3 + indicatorHalfWidth, getHeight() / 9 * 2);
//        centerIndicatorPath.lineTo(getWidth() / 6 * 3, getHeight() / 3 - 2);
//        centerIndicatorPath.lineTo(getWidth() / 6 * 3 - indicatorHalfWidth, getHeight() / 9 * 2);
//        centerIndicatorPath.close();
//
//        //right indicator
//        rightIndicatorPath.moveTo(getWidth() / 6 * 5 - indicatorHalfWidth, 0);
//        rightIndicatorPath.lineTo(getWidth() / 6 * 5 + indicatorHalfWidth, 0);
//        rightIndicatorPath.lineTo(getWidth() / 6 * 5 + indicatorHalfWidth, getHeight() / 9 * 2);
//        rightIndicatorPath.lineTo(getWidth() / 6 * 5, getHeight() / 3 - 2);
//        rightIndicatorPath.lineTo(getWidth() / 6 * 5 - indicatorHalfWidth, getHeight() / 9 * 2);
//        rightIndicatorPath.close();
//
//        if (indicatorLocation == 0) {
//            canvas.drawPath(leftIndicatorPath, mIndicatorPaint);
//        } else if (indicatorLocation == 1) {
//            canvas.drawPath(centerIndicatorPath, mIndicatorPaint);
//        } else if (indicatorLocation == 2) {
//            canvas.drawPath(rightIndicatorPath, mIndicatorPaint);
//        }
    }

    private void drawSection(Canvas canvas) {
        float sectionTop = indicatorHeight + indicatorAndSectionInterval;
        float sectionBottom = sectionTop + sectionHeight;
        //1.left section
//        canvas.drawRect(0, sectionTop, getWidth() / 3, getHeight() / 3 * 2, mLeftSectionPaint);
        mSectionPaint.setColor(leftSectionColor);
        canvas.drawRect(0, sectionTop, getWidth() / 3, sectionBottom, mSectionPaint);

        //2.center section
//        canvas.drawRect(getWidth() / 3, sectionTop, getWidth() / 3 * 2, getHeight() / 3 * 2, mCenterSectionPaint);
        mSectionPaint.setColor(centerSectionColor);
        canvas.drawRect(getWidth() / 3, sectionTop, getWidth() / 3 * 2, sectionBottom, mSectionPaint);

        //3.right section
//        canvas.drawRect(getWidth() / 3 * 2, sectionTop, getWidth(), getHeight() / 3 * 2, mRightSectionPaint);
        mSectionPaint.setColor(rightSectionColor);
        canvas.drawRect(getWidth() / 3 * 2, sectionTop, getWidth(), sectionBottom, mSectionPaint);
    }

    private void drawText(Canvas canvas) {
//        mTextPaint.setTextSize(getHeight() / 4);
        Rect leftTextBounds = new Rect();
        Rect centerTextBounds = new Rect();
        Rect rightTextBounds = new Rect();

        mTextPaint.setTextSize(textSize);
        mTextPaint.getTextBounds(leftText, 0, leftText.length(), leftTextBounds);
        mTextPaint.getTextBounds(centerText, 0, centerText.length(), centerTextBounds);
        mTextPaint.getTextBounds(rightText, 0, rightText.length(), rightTextBounds);
//        float y = getHeight() / 6 * 5 + (rightTextBounds.bottom - rightTextBounds.top) / 2;
        float y = getHeight() - (getHeight() - getPaddingTop() - indicatorHeight - indicatorAndSectionInterval -
                sectionHeight - sectionAndTextInterval) / 2 + (rightTextBounds.bottom - rightTextBounds.top) / 2;

        canvas.drawText(leftText,
                getWidth() / 6 - (leftTextBounds.right - leftTextBounds.left) / 2,
                y, mTextPaint);
        canvas.drawText(centerText,
                getWidth() / 6 * 3 - (centerTextBounds.right - centerTextBounds.left) / 2,
                y, mTextPaint);
        canvas.drawText(rightText,
                getWidth() / 6 * 5 - (rightTextBounds.right - rightTextBounds.left) / 2,
                y, mTextPaint);
    }


    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        mIndicatorPaint.setColor(indicatorColor);
        invalidate();
    }

    public int getLeftSectionColor() {
        return leftSectionColor;
    }

    public void setLeftSectionColor(int leftSectionColor) {
        this.leftSectionColor = leftSectionColor;
        invalidate();

    }

    public int getCenterSectionColor() {
        return centerSectionColor;
    }

    public void setCenterSectionColor(int centerSectionColor) {
        this.centerSectionColor = centerSectionColor;
        invalidate();
    }

    public int getRightSectionColor() {
        return rightSectionColor;
    }

    public void setRightSectionColor(int rightSectionColor) {
        this.rightSectionColor = rightSectionColor;
        invalidate();
    }

    public int getLeftTextColor() {
        return leftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
    }

    public int getCenterTextColor() {
        return centerTextColor;
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }

    public int getRightTextColor() {
        return rightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
        invalidate();
    }

    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidate();
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public int getIndicatorLocation() {
        return indicatorLocation;
    }

    public void setIndicatorLocation(int indicatorLocation) {
        this.indicatorLocation = indicatorLocation;
        invalidate();
    }

    public void setIndicatorLocation(IndicatorLocation location) {
        switch (location) {
            case LEFT:
                this.indicatorLocation = 0;
                break;
            case CENTER:
                this.indicatorLocation = 1;
                break;
            case RIGHT:
                this.indicatorLocation = 2;
                break;
        }
        invalidate();
    }

    enum IndicatorLocation {
        LEFT,
        CENTER,
        RIGHT
    }
}
