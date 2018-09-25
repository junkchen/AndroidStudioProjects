package com.junkchen.customchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple line chart
 * Created by Junk on 2017/8/29.
 */

public class LinesChart extends View {
    public static final String TAG = LinesChart.class.getSimpleName();

    private Paint mLinePaint;
    private Paint mPaint;
    private Paint mTextPaint;

    private List<List<LinePoint>> mOriginalLinesPoints = new ArrayList<>();//原始数据集合
    private List<List<PointF>> mWrapPoints = new ArrayList<>();//最终要显示的折线的坐标位置
    private List<NormalRange<Float>> normalRanges = new ArrayList<>();
    private float maxValue;
    private float minValue;
    private float xInterval;//图表中两个点之间的横坐标距离
    private float lineHeight;
    private float textHeight;//底部x坐标文字高度
    private int yAxisShowNumber = 6;//Y轴坐标线显示条数
    private int xAxisShowNumber = 5;//X轴坐标线显示条数
    private int showPointNumber = 50;//显示的点数量
    private boolean isShowTag = false;//是否显示触摸时的点的坐标

    private ValueAnimator mValueAnimator;

    //About text size
    private Paint.FontMetrics mFontMetrics;

    public LinesChart(Context context) {
        this(context, null);
    }

    public LinesChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.rgb(0x6B, 0xB5, 0x28));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.argb(0x28, 0x6B, 0xB5, 0x28));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(2);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 28,
                getResources().getDisplayMetrics());
        mTextPaint.setTextSize(textSize);
        Log.i(TAG, "init: --------------------------------------textSize(px) = " + textSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.GRAY);

        mFontMetrics = mTextPaint.getFontMetrics();
        Log.i(TAG, "init: top: " + mFontMetrics.top + ", bottom: " + mFontMetrics.bottom +
                ", ascent: " + mFontMetrics.ascent + ", descent: " + mFontMetrics.descent +
                ", leading: " + mFontMetrics.leading);
        Log.i(TAG, "init: b-t = " + (mFontMetrics.bottom - mFontMetrics.top));
        textHeight = mFontMetrics.bottom - mFontMetrics.top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);

        mLinePaint.setStrokeWidth(0.5f);
        mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));

        int chartWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int chartHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        lineHeight = chartHeight - textHeight;
        float hInterval = lineHeight * 1.0f / yAxisShowNumber;
        xInterval = chartWidth / (showPointNumber - 1);

        //Draw normal area background
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.argb(0x28, 0x32, 0xAC, 0xD7));
        for (NormalRange<Float> normalRange : normalRanges) {
            canvas.drawRect(getPaddingLeft(),
                    getPaddingTop() + lineHeight * (1 - (normalRange.max - minValue) * 1.0f / (maxValue - minValue)),
                    getPaddingLeft() + chartWidth,
                    getPaddingTop() + lineHeight * (1 - (normalRange.min - minValue) * 1.0f / (maxValue - minValue)),
                    mPaint);
            mPaint.setColor(Color.argb(0x28, 0x6B, 0xB5, 0x28));
        }
//        canvas.drawRect(getPaddingLeft(),
//                getPaddingTop() + lineHeight * (1 - (140 - minValue) * 1.0f / (maxValue - minValue)),
//                getPaddingLeft() + chartWidth,
//                getPaddingTop() + lineHeight * (1 - (100 - minValue) * 1.0f / (maxValue - minValue)),
//                mPaint);
//        mPaint.setColor(Color.argb(0x28, 0x6B, 0xB5, 0x28));
//        canvas.drawRect(getPaddingLeft(),
//                getPaddingTop() + lineHeight * (1 - (90 - minValue) * 1.0f / (maxValue - minValue)),
//                getPaddingLeft() + chartWidth,
//                getPaddingTop() + lineHeight * (1 - (60 - minValue) * 1.0f / (maxValue - minValue)),
//                mPaint);

        //Draw x coordinate
//        float xAxisLineInterval = chartWidth / (xAxisShowNumber + 1);
//        for (int i = 1; i <= xAxisShowNumber; i++) {
//            canvas.drawLine(i * xAxisLineInterval, 0, i * xAxisLineInterval, lineHeight, mLinePaint);
//        }
        int jump = showPointNumber / xAxisShowNumber;
        Rect bounds = new Rect();
        float xTextBaseline = getPaddingTop() + chartHeight - mFontMetrics.bottom;
        if (!mOriginalLinesPoints.isEmpty()) {
            List<LinePoint> linePoints = mOriginalLinesPoints.get(0);
            for (int i = 0; i < linePoints.size(); i++) {
                if (i % jump == jump / 2) {
                    String xStr = linePoints.get(i).getX();
                    mTextPaint.getTextBounds(xStr, 0, xStr.length(), bounds);
                    Log.i(TAG, "onDraw: -------actualTextHeight = " + (bounds.bottom - bounds.top));
                    canvas.drawText(xStr,
                            getPaddingLeft() + i * xInterval - (bounds.right - bounds.left) / 2,
                            xTextBaseline,
                            mTextPaint);
                    canvas.drawLine(getPaddingLeft() + i * xInterval,
                            getPaddingTop(),
                            getPaddingLeft() + i * xInterval,
                            getPaddingTop() + lineHeight,
                            mLinePaint);
                }
            }
        }

        //Draw y coordinate
        int yAxisInterval = (int) ((maxValue - minValue) / yAxisShowNumber);
        for (int i = 1; i < yAxisShowNumber + 1; i++) {
            canvas.drawLine(getPaddingLeft(),
                    getPaddingTop() + i * hInterval,
                    getPaddingLeft() + chartWidth,
                    getPaddingTop() + i * hInterval,
                    mLinePaint);
            if (i != yAxisShowNumber) {
                canvas.drawText(String.valueOf(minValue + yAxisInterval * (yAxisShowNumber - i)),
                        getPaddingLeft(), getPaddingTop() + i * hInterval, mTextPaint);
            }
        }

        //Draw broken line
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setColor(Color.rgb(0x32, 0xAC, 0xD7));
        for (List<PointF> pointFs : mWrapPoints) {
            if (pointFs.size() > 1) {
                Path linePath = new Path();
                for (int i = 0; i < pointFs.size(); i++) {//Heart rate range: 30~250 bpm
                    PointF point = pointFs.get(i);
                    if (i == 0) {
                        linePath.moveTo(point.x, point.y);
                    } else {
                        linePath.lineTo(point.x, point.y);
                    }
                }
                canvas.drawPath(linePath, mLinePaint);
            }
            mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
        }

        //Draw small circle
        for (int i = 0; i < mWrapPoints.size(); i++) {
            List<PointF> pointFs = mWrapPoints.get(i);
            for (PointF point : pointFs) {
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(point.x, point.y, 8, mPaint);
                if (i == 0) {
                    mPaint.setColor(Color.rgb(0x32, 0xAC, 0xD7));
                } else if (i == 1) {
                    mPaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
                }
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(point.x, point.y, 8, mPaint);
            }
        }

        //绘制tag
        for (int i = 0; i < mOriginalLinesPoints.size(); i++) {
            List<LinePoint> linePoints = mOriginalLinesPoints.get(i);
            List<PointF> pointFs = mWrapPoints.get(i);
            if (isShowTag && linePoints.size() != 0) {
//                mTextPaint.setColor(Color.RED);
//                if (i == 0) {
//                    canvas.drawText("y0: " + linePoints.get(tagIndex).getY()
//                            + ", x0: " + linePoints.get(tagIndex).getX(), 10, 25, mTextPaint);
//                } else {
//                    canvas.drawText("y1: " + linePoints.get(tagIndex).getY()
//                            + ", x1: " + linePoints.get(tagIndex).getX(), 10, 50, mTextPaint);
//                }

                //1.Draw vertical line (在离手指触摸X位置最近的位置绘制一条竖线及x的真实值)
                float tagXCoordinate = pointFs.get(tagIndex).x;
                mLinePaint.setStrokeWidth(1);
                canvas.drawLine(tagXCoordinate,
                        getPaddingTop(),
                        tagXCoordinate,
                        getPaddingTop() + lineHeight,
                        mLinePaint);
                String originalX = linePoints.get(tagIndex).getX();
                canvas.drawText(originalX,
                        tagXCoordinate - mTextPaint.measureText(originalX) / 2,
                        getPaddingTop() + chartHeight - mFontMetrics.bottom,
                        mTextPaint);

                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (i == 0) {
                    mPaint.setColor(Color.rgb(0x32, 0xAC, 0xD7));
                } else if (i == 1) {
                    mPaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
                }
                canvas.drawCircle(tagXCoordinate, pointFs.get(tagIndex).y, 7, mPaint);

                String yValueStr = String.valueOf(linePoints.get(tagIndex).getY());
                Rect yValueBounds = new Rect();
                mTextPaint.getTextBounds(yValueStr, 0, yValueStr.length(), yValueBounds);
                Log.i(TAG, "onDraw: left: " + yValueBounds.left + ", top: " + yValueBounds.top +
                        ", right: " + yValueBounds.right + ", bottom: " + yValueBounds.bottom);

                RectF textRoundRect = new RectF();
                textRoundRect.top = pointFs.get(tagIndex).y - textHeight - 24;
                textRoundRect.bottom = pointFs.get(tagIndex).y - 24;
                textRoundRect.left = tagXCoordinate -
                        (yValueBounds.right - yValueBounds.left) / 2 - 16;
                textRoundRect.right = tagXCoordinate +
                        (yValueBounds.right - yValueBounds.left) / 2 + 16;

                mPaint.setAlpha(225);
                canvas.drawRoundRect(textRoundRect,
                        textRoundRect.height() / 2, textRoundRect.height() / 2, mPaint);

                mTextPaint.setColor(Color.WHITE);
                canvas.drawText(yValueStr,
                        tagXCoordinate - (yValueBounds.right - yValueBounds.left) / 2,
                        textRoundRect.bottom - mFontMetrics.bottom, mTextPaint);
                mTextPaint.setColor(Color.GRAY);
            }
        }
    }

    public void setData(@NonNull List<List<LinePoint>> data) {
        if (data.isEmpty()) return;
        tagIndex = 0;
        startAnimation(data);
    }

    private int lastIndex = 0;

    private void startAnimation(final List<List<LinePoint>> data) {
        mValueAnimator = new ValueAnimator();
        mValueAnimator.setDuration(800);
        mValueAnimator.setIntValues(5, data.get(0).size());
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.start();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int toIndex = (int) animation.getAnimatedValue();
//                Log.i(TAG, "onAnimationUpdate: toIndex == lastIndex: " + (toIndex == lastIndex));
                if (toIndex == lastIndex) {
                    lastIndex = toIndex;
                    return;
                }
//                Log.i(TAG, "onAnimationUpdate: toIndex = " + toIndex);
                lastIndex = toIndex;
                mOriginalLinesPoints.clear();
                mWrapPoints.clear();
                //Calculate broken line axis point
                for (List<LinePoint> linePointList : data) {
                    List<LinePoint> linePoints = new ArrayList<>();
                    List<PointF> points = new ArrayList<>();
                    for (int i = 0; i < toIndex; i++) {
                        LinePoint linePoint = linePointList.get(i);
                        linePoints.add(linePoint);
                        float yValue = linePoint.getY();
                        float scale = 1 - (yValue - minValue) * 1.0f / (maxValue - minValue);
                        points.add(new PointF(getPaddingLeft() + xInterval * i,
                                getPaddingTop() + lineHeight * scale));
                    }
                    mOriginalLinesPoints.add(linePoints);
                    mWrapPoints.add(points);
                }

                invalidate();
            }
        });
    }

    public void setRange(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public List<NormalRange<Float>> getNormalRanges() {
        return normalRanges;
    }

    public void setNormalRanges(List<NormalRange<Float>> normalRanges) {
        this.normalRanges = normalRanges;
    }

    public int getyAxisShowNumber() {
        return yAxisShowNumber;
    }

    public void setyAxisShowNumber(int yAxisShowNumber) {
        this.yAxisShowNumber = yAxisShowNumber;
    }

    private int tagIndex = 0;//标签值的位置
    private float lastX;//上次触摸时的X的位置

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOriginalLinesPoints.isEmpty() || mValueAnimator.isRunning()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                lastX = downX;
                Log.i(TAG, "onTouchEvent: ACTION_DOWN x: " + downX);
                tagHandle(downX);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                Log.i(TAG, "onTouchEvent: ACTION_MOVE x: " + moveX);
                if (Math.abs(moveX - lastX) <= (xInterval / 3)) break;
                lastX = moveX;
                tagHandle(moveX);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: ACTION_UP x: " + event.getX());
                break;
        }
        return true;
    }

    private void tagHandle(float x) {
        int newTagIndex = findTagPosition(x);
        if (tagIndex == newTagIndex) {
            isShowTag = !isShowTag;
        } else {
            isShowTag = true;
            tagIndex = newTagIndex;
            if (mOnPointSelectChangedListener != null) {
                mOnPointSelectChangedListener.onPointSelectChanged(tagIndex);
            }
        }
        for (List<LinePoint> linePointList : mOriginalLinesPoints) {
            LinePoint linePoint = linePointList.get(tagIndex);
            Log.i(TAG, "tagHandle: 被选中：tagIndex = " + tagIndex +
                    ", y = " + linePoint.getY() + ", x = " + linePoint.getX());
        }
        invalidate();
    }

    /**
     * Find tag index position by touch x coordinate.
     *
     * @param x touch x coordinate
     * @return tag index
     */
    private int findTagPosition(float x) {
        List<PointF> pointFs = mWrapPoints.get(0);
        int newTagIndex = 0;
        for (int i = 0; i < pointFs.size(); i++) {
            if (i == (pointFs.size() - 1)) {
                newTagIndex = pointFs.size() - 1;
                break;
            }
            PointF point = pointFs.get(i);
            if (x <= point.x) {
                if (i == 0) {
                    newTagIndex = 0;
                } else {
                    float middle = (point.x + pointFs.get(i - 1).x) / 2;
                    if (x < middle) {
                        newTagIndex = i - 1;
                    } else {
                        newTagIndex = i;
                    }
                }
                break;
            }
        }
        return newTagIndex;
    }

    public int getShowPointNumber() {
        return showPointNumber;
    }

    public void setShowPointNumber(int showPointNumber) {
        this.showPointNumber = showPointNumber;
    }

    /**
     * Which point was selected.
     */
    public interface OnPointSelectChangedListener {
        void onPointSelectChanged(int index);
    }

    private OnPointSelectChangedListener mOnPointSelectChangedListener;

    public void setOnPointSelectChangedListener(OnPointSelectChangedListener listener) {
        this.mOnPointSelectChangedListener = listener;
    }
}
