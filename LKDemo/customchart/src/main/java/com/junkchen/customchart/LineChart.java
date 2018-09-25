package com.junkchen.customchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junk on 2017/8/29.
 * 1、好健康荔康伴
 * 2、荔康知你健康
 * 3、您的健康在这里，荔康！
 * 4、健康预防在荔康！
 */

public class LineChart extends View {
    public static final String TAG = LineChart.class.getSimpleName();

    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mPaint;
    private Paint mGradientPaint;

    private ArrayList<LinePoint> linePoints = new ArrayList<>();
    private List<Point> points = new ArrayList<>();//最终要显示的折线的坐标位置
    private NormalRange<Integer> normalRange;
    private int maxValue;
    private int minValue;
    private float xInterval;//图表中两个点之间的横坐标距离
    private float lineHeight;
    private float textHeight;//底部x坐标文字高度
    private int yAxisShowNumber = 6;//Y轴坐标线显示条数
    private int xAxisShowNumber = 5;//X轴坐标线显示条数
    private int showPointNumber = 50;//显示的点数量
    private boolean isShowTag = false;//是否显示触摸时的点的坐标

    private ValueAnimator mValueAnimator;

    //About text size
    private Paint.FontMetrics mfontMetrics;

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient linearGradient = new LinearGradient(
                getWidth() / 2, 0, getWidth() / 2, getHeight(),
                0xFF7AB842, 0x33FFFFFF, Shader.TileMode.CLAMP);//
        mGradientPaint.setShader(linearGradient);
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.rgb(0x6B, 0xB5, 0x28));

        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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

        mfontMetrics = mTextPaint.getFontMetrics();
        Log.i(TAG, "init: top: " + mfontMetrics.top + ", bottom: " + mfontMetrics.bottom +
                ", ascent: " + mfontMetrics.ascent + ", descent: " + mfontMetrics.descent +
                ", leading: " + mfontMetrics.leading);
        Log.i(TAG, "init: b-t = " + (mfontMetrics.bottom - mfontMetrics.top));
        textHeight = mfontMetrics.bottom - mfontMetrics.top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);

        mLinePaint.setStrokeWidth((float) 0.5);
        mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));

        int chartWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int chartHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        lineHeight = chartHeight - textHeight;
        float hInterval = lineHeight * 1.0f / yAxisShowNumber;
        xInterval = chartWidth / (showPointNumber - 1);

        //Draw normal area background
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.argb(0x28, 0x6B, 0xB5, 0x28));
        if (normalRange != null)
            canvas.drawRect(getPaddingLeft(),
                    getPaddingTop() + lineHeight * (1 - (normalRange.max - minValue) * 1.0f / (maxValue - minValue)),
                    getPaddingLeft() + chartWidth,
                    getPaddingTop() + lineHeight * (1 - (normalRange.min - minValue) * 1.0f / (maxValue - minValue)),
                    mPaint);

        //Draw x coordinate
//        float xAxisLineInterval = chartWidth / (xAxisShowNumber + 1);
//        for (int i = 1; i <= xAxisShowNumber; i++) {
//            canvas.drawLine(i * xAxisLineInterval, 0, i * xAxisLineInterval, lineHeight, mLinePaint);
//        }
//        int jump = linePoints.size() / xAxisShowNumber;
        int jump = showPointNumber / xAxisShowNumber;

        Rect bounds = new Rect();
        float xTextBaseline = getPaddingTop() + chartHeight - mfontMetrics.bottom;
        for (int i = 0; i < linePoints.size(); i++) {
            if (i % jump == jump / 2) {
                String xStr = linePoints.get(i).getX();
                mTextPaint.getTextBounds(xStr, 0, xStr.length(), bounds);
                Log.i(TAG, "onDraw: -----------actualTextHeight = " + (bounds.bottom - bounds.top));
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

        //Draw y coordinate
        int yAxisInterval = (maxValue - minValue) / yAxisShowNumber;
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
        mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
        if (points.size() > 1) {
            Path linePath = new Path();
            for (int i = 0; i < points.size(); i++) {//Heart rate range: 30~250 bpm
                Point point = points.get(i);
                if (i == 0) {
                    linePath.moveTo(point.x, point.y);
                } else {
                    linePath.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(linePath, mLinePaint);

            linePath.lineTo(points.get(points.size() - 1).x, getPaddingTop() + lineHeight);
            linePath.lineTo(getPaddingLeft(), getPaddingTop() + lineHeight);
            linePath.close();
            canvas.drawPath(linePath, mGradientPaint);
        }

        //Draw small circle
        for (Point point : points) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(point.x, point.y, 8, mPaint);
            mPaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(point.x, point.y, 8, mPaint);
        }

        //绘制tag
        if (isShowTag && linePoints.size() != 0) {
            mTextPaint.setColor(Color.RED);
            canvas.drawText("y: " + linePoints.get(tagIndex).getY()
                    + ", x: " + linePoints.get(tagIndex).getX(), 10, 30, mTextPaint);

            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//            mPaint.setColor(Color.RED);
            canvas.drawCircle(points.get(tagIndex).x, points.get(tagIndex).y, 7, mPaint);

            String yValueStr = String.valueOf(linePoints.get(tagIndex).getY());
            Rect yValueBounds = new Rect();
            mTextPaint.getTextBounds(yValueStr, 0, yValueStr.length(), yValueBounds);
            Log.i(TAG, "onDraw: left: " + yValueBounds.left + ", top: " + yValueBounds.top +
                    ", right: " + yValueBounds.right + ", bottom: " + yValueBounds.bottom);

            RectF textRoundRect = new RectF();
            textRoundRect.top = points.get(tagIndex).y - textHeight - 24;
            textRoundRect.bottom = points.get(tagIndex).y - 24;
            textRoundRect.left = points.get(tagIndex).x -
                    (yValueBounds.right - yValueBounds.left) / 2 - 16;
            textRoundRect.right = points.get(tagIndex).x +
                    (yValueBounds.right - yValueBounds.left) / 2 + 16;

            canvas.drawRoundRect(textRoundRect,
                    textRoundRect.height() / 2, textRoundRect.height() / 2, mPaint);

            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(yValueStr,
                    points.get(tagIndex).x - (yValueBounds.right - yValueBounds.left) / 2,
                    textRoundRect.bottom - mfontMetrics.bottom, mTextPaint);
            mTextPaint.setColor(Color.GRAY);
        }
    }

    private void drawSample(Canvas canvas) {
        mLinePaint.setStrokeWidth((float) 0.5);
        mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));

        int width = getWidth();
        int height = getHeight();
        int wInterval = width / 6;
        int hInterval = height / 8;

        canvas.drawRect(0, hInterval * 2, width, hInterval * 5, mPaint);

        for (int i = 0; i < 6; i++) {
            canvas.drawLine(i * wInterval, 0, i * wInterval, height, mLinePaint);
        }

        for (int i = 0; i < 8; i++) {
            canvas.drawLine(0, i * hInterval, width, i * hInterval, mLinePaint);
        }

        mLinePaint.setStrokeWidth(4);
        mLinePaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
        Path linePath = new Path();
        linePath.moveTo(2, hInterval * 4);
        linePath.lineTo(wInterval * 1, hInterval * 5);
        linePath.lineTo(wInterval * 2, hInterval * 4);
        linePath.lineTo(wInterval * 3, hInterval * 6);
        linePath.lineTo(wInterval * 4, hInterval * 3);
        linePath.lineTo(wInterval * 5, hInterval * 4);
        canvas.drawPath(linePath, mLinePaint);

        List<Point> points = new ArrayList<>();
        points.add(new Point(2, hInterval * 4));
        points.add(new Point(wInterval * 1, hInterval * 5));
        points.add(new Point(wInterval * 2, hInterval * 4));
        points.add(new Point(wInterval * 3, hInterval * 6));
        points.add(new Point(wInterval * 4, hInterval * 3));
        points.add(new Point(wInterval * 5, hInterval * 4));

        for (Point point : points) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(point.x, point.y, 8, mPaint);
            mPaint.setColor(Color.rgb(0x3E, 0xD7, 0x84));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(point.x, point.y, 8, mPaint);
        }
    }

    public void setData(@NonNull ArrayList<LinePoint> linePoints) {
//        this.linePoints = linePoints;
//        invalidate();

//        int lineHeight = getMeasuredHeight() - 20;
//        int xInterval = getMeasuredWidth() / (50 - 1);
//        points.clear();
//        for (int i = 0; i < linePoints.size(); i++) {
//            Integer y = linePoints.get(i).getY();
//            float scale = 1 - (y - minValue) * 1.0f / (maxValue - minValue);
//            points.add(new Point(xInterval * i, (int) (lineHeight * scale)));
//        }

        tagIndex = 0;
        startAnimation(linePoints);
    }

    private int lastIndex = 0;

    private void startAnimation(final ArrayList<LinePoint> allLinePoints) {
        mValueAnimator = new ValueAnimator();
        mValueAnimator.setDuration(800);
        mValueAnimator.setIntValues(5, allLinePoints.size());
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.start();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int toIndex = (int) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: toIndex == lastIndex: " + (toIndex == lastIndex));
                if (toIndex == lastIndex) {
                    lastIndex = toIndex;
                    return;
                }
                Log.i(TAG, "onAnimationUpdate: toIndex = " + toIndex);
                lastIndex = toIndex;
                linePoints.clear();
                points.clear();
                //Calculate broken line axis point
                for (int i = 0; i < toIndex; i++) {
                    LinePoint linePoint = allLinePoints.get(i);
                    linePoints.add(linePoint);
                    Float y = (Float) linePoint.getY();
                    float scale = 1 - (y - minValue) * 1.0f / (maxValue - minValue);
                    points.add(new Point((int) (getPaddingLeft() + xInterval * i),
                            (int) (getPaddingTop() + lineHeight * scale)));
                }

                invalidate();
            }
        });
    }

    public void setRange(int maxValue, int minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public NormalRange<Integer> getNormalRange() {
        return normalRange;
    }

    public void setNormalRange(NormalRange<Integer> normalRange) {
        this.normalRange = normalRange;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
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
        if (linePoints.isEmpty() || mValueAnimator.isRunning()) return super.onTouchEvent(event);
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
        }
        LinePoint linePoint2 = linePoints.get(tagIndex);
        Log.i(TAG, "tagHandle: 被选中：tagIndex = " + tagIndex +
                ", y = " + linePoint2.getY() + ", x = " + linePoint2.getX());
        invalidate();
    }

    /**
     * Find tag index position by touch x coordinate.
     *
     * @param x touch x coordinate
     * @return tag index
     */
    private int findTagPosition(float x) {
        int newTagIndex = 0;
        for (int i = 0; i < points.size(); i++) {
            if (i == (points.size() - 1)) {
                newTagIndex = points.size() - 1;
                break;
            }
            Point point = points.get(i);
            if (x <= point.x) {
                if (i == 0) {
                    newTagIndex = 0;
                } else {
                    int middle = (point.x + points.get(i - 1).x) / 2;
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
}
