package com.junkchen.hetdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.junkchen.hetdemo.DensityUtil;
import com.junkchen.hetdemo.R;

import java.math.BigDecimal;


/**
 * 闹钟实现
 * 有开始和结束开关
 * 可以使用枚举类型增加各种闹钟式样
 * 有关闭和打开状态
 * 5分钟为一个单位滑动
 * 作者： linhongfei}
 * 日期： 2017/3/28.
 */
public class AlarmClockView extends View implements View.OnClickListener {

    private static final int CLOSETEXTCOLOR = Color.parseColor("#99000000");
    private static final int CLOSEGDUCOLOR = Color.parseColor("#66000000");
    private static final int BIGCOLOR = Color.parseColor("#17000000");
    private static final int SMALLCOLOR = Color.BLACK;
    private static final int BIGGRADUTIONCOLOR = Color.WHITE;
    private static final int SMALLGRADUTIONCOLOR = Color.WHITE;
    private static final int GDTTEXTCOLOR = Color.WHITE;
    private static final int TIMETEXTCOLOR = Color.WHITE;

    /**
     * 默认填充区大小
     */
    private static final float BIG_STEOKE_mWidth = 80.0f;
    //一个钟头占的度数360/12
    private static final int TIME_UNTI = 30;
    //5分钟为单位滑动
    private static final int MOVE_UNTI = 5;
    //默认刻度数
    private static final int GRADUATION_NUMBER = 60;
    //刻度宽度
    private static final int GRADUATION_WIDTH = 4;
    //刻度宽度
    private static final int TIME_WIDTH = 10;
    //控件最小大小
    private static final int MINSIZE = 800;
    //默认刻度字大小
    private static final int GDT_TEXTSIZE = 40;
    //默认时间字大小
    private static final int TIME_TEXTSIZE = 60;
    //默认触摸范围宽度增加20
    private static final int TOUCH_mWidth = 20;
    //画边画笔
    private Paint mBigPaint;
    //颜色
    private int mBigColor;
    //画实心圆画笔
    private Paint mSmallPaint;
    //画实心圆画笔
    private Paint mDrawableCirclePaint;

    private int mSmallColor;
    //又粗又长的钟表刻度画笔
    private Paint mBigGraduationPaint;

    private int mBigGraduationColor;
    //又短又小的钟表刻度画笔
    private Paint mSmallGraduationPaint;
    private int mSmallGraduationColor;
    //刻度文字画笔
    private Paint mGdtTextPaint;
    private int mGdtTextColor;
    //时间文字画笔
    private Paint mTimeTextPaint;
    //大时间文字画笔
    private Paint mBigTimeTextPaint;
    private int mTimeTextColro;
    //开始到结束的弧度画笔
    private Paint mArcPaint;
    //控件大小
    private int mHeight;
    //控件大小
    private int mWidth;
    //大圆形描边大小
    private float mStrokeWidth;
    //刻度宽度
    private int mGdtmWidth;
    //时间画笔宽度
    private int mTimeWidth;
    //时间画笔宽度
    private int mBigTimeWidth;
    //刻度字体大小
    private int mGdtTextSize;
    //刻度字体大小
    private int mTimeTextSize;
    //刻度字体大小
    private int mBigTimeTextSize;
    //圆的半径
    private float mCircleRadius;
    //刻度数
    private int mGraduationNumbers;
    //默认开始开关在9.5的位置，12个小时为单位
    private float mStartArc = 21.5f;
    //默认开始结束在7.5的位置
    private float mEndArc = 7.5f;
    //开始图片
    private Drawable mStartAlarmClockDrawable;
    //结束图片
    private Drawable mEndAlarmClockDrawable;
    //开始图片所在的位置
    private Rect mStartDrawablePosition;
    //结束图片所在的位置
    private Rect mEndDrawablePosition;

    private Context mContext;
    //弧形范围大小
    private RectF mArcRect;
    //按下时的xy坐标
    private float mDownX, mDownY;
    //0，开始图标，1结束图标，-1都没有触摸到
    private int mTouchFlag;
    //圆点
    private float mCx;

    private float mCy;
    //触摸旋转的角度
    private double mMoveAngle;
    //滑动的度数单位
    private float moveDegreeUnti;
    //滑动一次的时间单位
    private int moveUnti;

    private SweepGradient mSweepGradient;

    private int[] mArcColors;

    private boolean isOpen;

    private Typeface typeface;

    private TimeCallBackListener mTimeCallBackListener;

    public AlarmClockView(Context context) {
        this(context, null);
    }

    public AlarmClockView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AlarmClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        mContext = context;

        isOpen = true;

        mGraduationNumbers = GRADUATION_NUMBER;
        mStrokeWidth = BIG_STEOKE_mWidth;
        mGdtmWidth = GRADUATION_WIDTH;
        mTimeWidth = GRADUATION_WIDTH;
        mBigTimeWidth = TIME_WIDTH;
        mGdtTextSize = GDT_TEXTSIZE;
        mTimeTextSize = TIME_TEXTSIZE;
        mBigTimeTextSize = TIME_TEXTSIZE;
        moveUnti = MOVE_UNTI;
        moveDegreeUnti = moveUnti * TIME_UNTI / 60.0f;

        mStartDrawablePosition = new Rect();
        mEndDrawablePosition = new Rect();

        mBigColor = BIGCOLOR;
        mSmallColor = SMALLCOLOR;
        mBigGraduationColor = BIGGRADUTIONCOLOR;
        mSmallGraduationColor = SMALLGRADUTIONCOLOR;
        mGdtTextColor = GDTTEXTCOLOR;
        mTimeTextColro = TIMETEXTCOLOR;

//        typeface = Typeface.createFromAsset(mContext.getAssets(), DolphinConstant.FONTS_PATH);
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/SourceHanSansCN-Light.otf");

        initAttrs(attrs, defStyleAttr);

        initPaint();

        //这里什么都没执行，只是为了在滚动view中获取事件，后面可以添加点击接口，或者做拦截
        this.setOnClickListener(this);
    }

    private void initPaint() {
        mBigPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigPaint.setStyle(Paint.Style.STROKE);
        mBigPaint.setColor(mBigColor);
        mBigPaint.setStrokeWidth(mStrokeWidth);

        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(5.5f, BlurMaskFilter.Blur.INNER);
        mSmallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallPaint.setStyle(Paint.Style.FILL);
        mSmallPaint.setColor(mSmallColor);
        mSmallPaint.setMaskFilter(blurMaskFilter);
        mSmallPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        // mSmallPaint.setShader(mSweepGradient);

        mBigGraduationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigGraduationPaint.setStyle(Paint.Style.FILL);
        mBigGraduationPaint.setColor(mBigGraduationColor);
        mBigGraduationPaint.setStrokeWidth(mGdtmWidth);

        mSmallGraduationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallGraduationPaint.setStyle(Paint.Style.FILL);
        mSmallGraduationPaint.setColor(mSmallGraduationColor);
        mSmallGraduationPaint.setStrokeWidth(mGdtmWidth);

        mGdtTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGdtTextPaint.setStyle(Paint.Style.FILL);
        mGdtTextPaint.setColor(mGdtTextColor);
        mGdtTextPaint.setStrokeWidth(mGdtmWidth);
        mGdtTextPaint.setTextSize(mGdtTextSize);
        mGdtTextPaint.setTypeface(typeface);

        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setStyle(Paint.Style.FILL);
        mTimeTextPaint.setColor(mTimeTextColro);
        mTimeTextPaint.setStrokeWidth(mTimeWidth);
        mTimeTextPaint.setTextSize(mTimeTextSize);
        mTimeTextPaint.setTypeface(typeface);

        mBigTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigTimeTextPaint.setStyle(Paint.Style.FILL);
        mBigTimeTextPaint.setColor(mTimeTextColro);
        mBigTimeTextPaint.setStrokeWidth(mBigTimeWidth);
        mBigTimeTextPaint.setTextSize(mBigTimeTextSize);
        mBigTimeTextPaint.setTypeface(typeface);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mArcPaint.setStrokeWidth(mStrokeWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mDrawableCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawableCirclePaint.setStyle(Paint.Style.FILL);
        mDrawableCirclePaint.setAntiAlias(true);
        mDrawableCirclePaint.setDither(true);
        mDrawableCirclePaint.setStrokeWidth(0);
        mDrawableCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawableCirclePaint.setColor(Color.parseColor("#40000000"));
    }

    /***
     * 闹钟关闭状态颜色处理
     */
    private void clockClose() {
        mBigPaint.setColor(BIGCOLOR);
        mBigGraduationPaint.setColor(CLOSEGDUCOLOR);
        mSmallGraduationPaint.setColor(CLOSEGDUCOLOR);
        mGdtTextPaint.setColor(CLOSETEXTCOLOR);
        mTimeTextPaint.setColor(CLOSETEXTCOLOR);
        mBigTimeTextPaint.setColor(CLOSETEXTCOLOR);
    }

    private void clockOpen() {
        mBigPaint.setColor(mBigColor);
        mBigGraduationPaint.setColor(mBigGraduationColor);
        mSmallGraduationPaint.setColor(mSmallGraduationColor);
        mGdtTextPaint.setColor(mGdtTextColor);
        mTimeTextPaint.setColor(mTimeTextColro);
        mBigTimeTextPaint.setColor(mTimeTextColro);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.AlarmClock_style, defStyleAttr, 0);
            mBigColor = a.getColor(R.styleable.AlarmClock_style_bigStroke_color, BIGCOLOR);
            mStrokeWidth = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_bigStroke_width, (int) mStrokeWidth);
            mSmallColor = a.getColor(R.styleable.AlarmClock_style_smallCircle_color, SMALLCOLOR);
            mBigGraduationColor = a.getColor(R.styleable.AlarmClock_style_bigGraduation_colro, BIGGRADUTIONCOLOR);
            mSmallGraduationColor = a.getColor(R.styleable.AlarmClock_style_smallGraduation_colro, SMALLGRADUTIONCOLOR);
            mGdtTextColor = a.getColor(R.styleable.AlarmClock_style_textGraduation_colro, GDTTEXTCOLOR);
            mTimeTextColro = a.getColor(R.styleable.AlarmClock_style_textTime_colro, TIMETEXTCOLOR);
            mGdtmWidth = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_graduation_width, mGdtmWidth);
            mGdtTextSize = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_graduation_textsize, mGdtTextSize);
            mTimeWidth = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_bigtime_width, mBigTimeWidth);
            mBigTimeWidth = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_time_width, mTimeWidth);
            mTimeTextSize = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_time_textsize, mTimeTextSize);
            mBigTimeTextSize = a.getDimensionPixelOffset(R.styleable.AlarmClock_style_bigtime_textsize, mBigTimeTextSize);
            mStartArc = a.getFloat(R.styleable.AlarmClock_style_starttime, mStartArc);
            mEndArc = a.getFloat(R.styleable.AlarmClock_style_endtime, mEndArc);
            mStartAlarmClockDrawable = a.getDrawable(R.styleable.AlarmClock_style_starttime_drawable);
            mEndAlarmClockDrawable = a.getDrawable(R.styleable.AlarmClock_style_endtime_drawable);
            mArcColors = mContext.getResources().getIntArray(a.getInt(R.styleable.AlarmClock_style_arc_colro, R.array.alarmclock_arcColors));
            isOpen = a.getBoolean(R.styleable.AlarmClock_style_open, isOpen);

            if (mArcColors == null) {
                mArcColors = mContext.getResources().getIntArray(R.array.alarmclock_arcColors);
            }

            if (mStartAlarmClockDrawable == null)
                mStartAlarmClockDrawable = mContext.getResources().getDrawable(R.drawable.logo);
            if (mEndAlarmClockDrawable == null)
                mEndAlarmClockDrawable = mContext.getResources().getDrawable(R.drawable.logo);

            if (!isOpen)
                clockClose();
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int mWidthMeasureSpec, int mHeightMeasureSpec) {
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
        mHeight = measure(mHeightMeasureSpec);
        mWidth = measure(mWidthMeasureSpec);
        mCircleRadius = mHeight / 2.0f - mStrokeWidth - 4;
        //设置最终大小
        setMeasuredDimension(mWidth, mHeight);

        mCx = mWidth / 2.0f;
        mCy = mHeight / 2.0f;

    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = MINSIZE;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layoutId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(mCx, mCy, mCircleRadius, mSmallPaint);
        //这里为毛加上画笔大小的一半呢？经过测试，发现他妹的总的大小会在原来的大小上面加上画笔大小的一半
        canvas.drawCircle(mCx, mCy, mCircleRadius + mStrokeWidth / 2.0f, mBigPaint);
        drawGraduation(canvas);
        if (isOpen) {
            drawArcStart2End(canvas, mStartArc, mEndArc);
            drawEndDrawable(canvas, mEndArc);
            drawStartDrawable(canvas, mStartArc);
        }

        drawTime(canvas, mStartArc, mEndArc);

        //这里必须加上，不然做图层混合会显示默认背景
        canvas.restoreToCount(layoutId);
    }

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawGraduation(Canvas canvas) {
        for (int i = 1; i <= mGraduationNumbers; i++) {
            //保存当前画布状态
            canvas.save();
            //以圆中心点为旋转原点，旋转画布
            canvas.rotate(i * (360 / mGraduationNumbers), mCx, mCy);
            if (i % (mGraduationNumbers / 12) == 0) {//又粗又大的刻度
                canvas.drawLine(mCx, mStrokeWidth + 30, mCx, mStrokeWidth + 45, mBigGraduationPaint);
                String number = String.valueOf((i - 1) / (mGraduationNumbers / 12) + 1);
                Rect rect = new Rect();
                mGdtTextPaint.getTextBounds(number, 0, number.length(), rect);
                canvas.rotate(-i * (360 / mGraduationNumbers), mCx, mStrokeWidth + 54 + rect.height() / 2.0f);
                canvas.drawText(number, mCx - rect.width() / 2.0f, mStrokeWidth + 54 + rect.height(), mGdtTextPaint);
            }
//            else{//又小又短的刻度
//               // canvas.drawLine(mCx,mStrokeWidth+5,mWidth/2.0f,mStrokeWidth+15,mSmallGraduationPaint);
//            }
            //回复原始状态
            canvas.restore();
        }
    }

    /**
     * 画弧形
     *
     * @param canvas
     * @param startArc
     * @param endArc
     */
    private void drawArcStart2End(Canvas canvas, float startArc, float endArc) {
        canvas.save();
        float last_mStartArc = startArc;
        float last_mEndArc = endArc;
        if (mArcRect == null)
            mArcRect = new RectF();
        mArcRect.set(mCx - (mCircleRadius + mStrokeWidth / 2.0f), mCy - (mCircleRadius + mStrokeWidth / 2.0f)
                , mCx + (mCircleRadius + mStrokeWidth / 2.0f), mCy + (mCircleRadius + mStrokeWidth / 2.0f));
        //右边正中间为0度，所以此刻应该是3.0的位置为0度
        if (last_mStartArc > 12)
            last_mStartArc -= 12;
        else if (last_mStartArc < 0)
            last_mStartArc += 12;

        if (last_mEndArc > 12)
            last_mEndArc -= 12;
        else if (last_mEndArc < 0)
            last_mEndArc += 12;

        int[] start = calculateHourAndMinute(mStartArc);
        int[] end = calculateHourAndMinute(mEndArc);
        if (start[0] == end[0] && start[1] == end[1] || (Math.abs(start[0] - end[0]) == 12 && start[1] == end[1])) {
            return;
        }

        if (mArcColors.length == 1) {
            mArcPaint.setColor(mArcColors[0]);
        } else {
            mSweepGradient = new SweepGradient(mCx, mCy, mArcColors, null);
            mArcPaint.setShader(mSweepGradient);
        }
        float startAnagle = (last_mStartArc - 3.0f) * TIME_UNTI;
        float endAnagle = 0;
        canvas.rotate(startAnagle, mCx, mCy);
        if (last_mStartArc > last_mEndArc) {
            endAnagle = 360 - (last_mStartArc - last_mEndArc) * TIME_UNTI;
        } else {
            endAnagle = (last_mEndArc - last_mStartArc) * TIME_UNTI;
        }
        //因为0度是在3点钟的位置
        canvas.drawArc(mArcRect, 0, endAnagle, false, mArcPaint);
        canvas.restore();
    }

    /**
     * 画开关图片
     *
     * @param canvas
     * @param arc    时间点
     */
    private void drawStartDrawable(Canvas canvas, float arc) {
        canvas.save();
        //旋转到开始开关的位置,
        canvas.rotate(arc * TIME_UNTI, mCx, mCy);
        canvas.rotate(-arc * TIME_UNTI, mCx, mStrokeWidth / 2.0f + 4);
        //开始画开始开关的图标
        mStartDrawablePosition.set((int) (mCx - mStrokeWidth / 2.0f), 4
                , (int) (mCx + mStrokeWidth / 2.0f), (int) (mStrokeWidth));
        mStartAlarmClockDrawable.setBounds(mStartDrawablePosition);
        mDrawableCirclePaint.reset();
        mDrawableCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(mCx, mStrokeWidth / 2.0f + 4, mStrokeWidth / 2.0f - 4, mDrawableCirclePaint);

        mStartAlarmClockDrawable.draw(canvas);
        canvas.restore();
        mStartDrawablePosition = rotateAngle(mStartDrawablePosition, arc * TIME_UNTI);
    }

    /**
     * 画开关图片
     *
     * @param canvas
     * @param arc    时间点
     */
    private void drawEndDrawable(Canvas canvas, float arc) {
        canvas.save();
        //旋转到结束开关的位置,
        canvas.rotate(arc * TIME_UNTI, mCx, mCy);
        canvas.rotate(-arc * TIME_UNTI, mCx, mStrokeWidth / 2.0f + 4);
        //开始画结束开关的图标
        mEndDrawablePosition.set((int) (mCx - mStrokeWidth / 2.0f), 4
                , (int) (mCx + mStrokeWidth / 2.0f), (int) (mStrokeWidth));
        mEndAlarmClockDrawable.setBounds(mEndDrawablePosition);

        mDrawableCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(mCx, mStrokeWidth / 2.0f + 4, mStrokeWidth / 2.0f - 4, mDrawableCirclePaint);

        mEndAlarmClockDrawable.draw(canvas);

        canvas.restore();
        mEndDrawablePosition = rotateAngle(mEndDrawablePosition, arc * TIME_UNTI);

    }

    /***
     * 画表中间的时间差
     * @param canvas
     * @param startArc
     * @param endArc
     */
    private void drawTime(Canvas canvas, float startArc, float endArc) {
        int[] times = null;
        if (startArc > endArc) {
            times = calculateTimeLength(24 - startArc + endArc);
        } else if (startArc < endArc) {
            times = calculateTimeLength(endArc - startArc);
        } else {
            times = new int[2];
            times[0] = 24;
            times[1] = 0;
        }
        if (times != null) {
            String timeMin = String.valueOf(times[1]);
            if (times[1] < 10) {//个位分数前面补0动作
                timeMin = "0" + timeMin;
            }

            //时间总长度,因为有三个间隔，每个间隔15px
            int timeWidth = DensityUtil.dip2px(mContext, 9);
            Rect rectTimeHour = new Rect();
            mBigTimeTextPaint.getTextBounds(String.valueOf(times[0]), 0, String.valueOf(times[0]).length(), rectTimeHour);

            Rect rectTimeMin = new Rect();
            mBigTimeTextPaint.getTextBounds(timeMin, 0, timeMin.length(), rectTimeMin);

            timeWidth += rectTimeHour.width();
            timeWidth += rectTimeMin.width();

            Rect rectH = new Rect();
            mTimeTextPaint.getTextBounds("h", 0, 1, rectH);
            timeWidth += rectH.width();

            Rect rectMin = new Rect();
            mTimeTextPaint.getTextBounds("m", 0, 1, rectMin);
            timeWidth += rectMin.width();

            int centerWidth = timeWidth / 2;
            //记录当前已画的跨度
            int currentWidth = 0;

            int textBaseLine = 0;


            canvas.drawText(String.valueOf(times[0]), 0, String.valueOf(times[0]).length(), mCx - centerWidth + currentWidth, mCy + rectTimeHour.height() / 2f, mBigTimeTextPaint);
            currentWidth += rectTimeHour.width();
            textBaseLine = rectTimeHour.height() / 2;

            currentWidth += DensityUtil.dip2px(mContext, 3);
            canvas.drawText("h", 0, 1, mCx - centerWidth + currentWidth, mCy + textBaseLine, mTimeTextPaint);
            currentWidth += rectH.width();
            currentWidth += DensityUtil.dip2px(mContext, 3);

            canvas.drawText(timeMin, 0, timeMin.length(), mCx - centerWidth + currentWidth, mCy + rectTimeMin.height() / 2f, mBigTimeTextPaint);
            currentWidth += rectTimeMin.width();
            if (textBaseLine == 0)
                textBaseLine = rectTimeMin.height() / 2;

            currentWidth += DensityUtil.dip2px(mContext, 3);
            canvas.drawText("m", 0, 1, mCx - centerWidth + currentWidth, mCy + textBaseLine, mTimeTextPaint);
            currentWidth += rectMin.width();

        }

    }

    /**
     * 将时间转成int数组
     *
     * @param time
     * @return
     */
    private int[] timeToint(String time) {
        if (TextUtils.isEmpty(time))
            return null;
        int[] times = new int[time.length()];
        for (int i = 0; i < times.length; i++) {
            times[i] = Integer.parseInt(String.valueOf(time.charAt(i)));
        }
        return times;
    }

    /**
     * 计算时间字体的宽度
     *
     * @param time
     * @return
     */
    private int caculateTimeWidth(int[] time) {
        if (time == null)
            return 0;
        int timeWidth = 0;
        for (int i : time) {
            Rect rectH = new Rect();
            mBigTimeTextPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), rectH);
            timeWidth += rectH.width();
        }
        return timeWidth;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mMoveAngle = Math.atan2(event.getX() - mCx, event.getY() - mCy);
                boolean touchStart = isPointInRecf(new Point((int) mDownX, (int) mDownY), mStartDrawablePosition);
                boolean touchEnd = isPointInRecf(new Point((int) mDownX, (int) mDownY), mEndDrawablePosition);
                if (touchStart) {
                    mTouchFlag = 0;
                } else if (touchEnd) {
                    mTouchFlag = 1;
                } else {
                    mTouchFlag = -1;
                }
                if (!isOpen)
                    mTouchFlag = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                //计算出当前滑动的角度斜率
                double dAngle = Math.atan2(event.getX() - mCx, event.getY() - mCy) - mMoveAngle;
                //将角度换算成度数
                float dDegree = (float) (180 * dAngle / Math.PI);
                //换算角度，永远取最小的
                if (dDegree > 90)
                    dDegree = 360 - dDegree;
                else if (dDegree < -90)
                    dDegree = -(360 + dDegree);
                //利用4舍5入获取要转动的分钟中的倍数,超过1.25度才开动动图标
                int lastDegree = 0;
                dDegree = dDegree / moveDegreeUnti;
                if (dDegree > 1 || dDegree < -1)
                    lastDegree = new BigDecimal(dDegree).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                else if (dDegree < -0.5)
                    lastDegree = -1;
                else if (dDegree > 0.5)
                    lastDegree = 1;
                if (lastDegree != 0)
                    mMoveAngle = Math.atan2(event.getX() - mCx, event.getY() - mCy);

                dDegree = lastDegree * moveDegreeUnti;

                if (mTouchFlag == 0) {////在这个距离范围内，以五分钟为单位转动,5分钟为2.5度
                    //计算出最后在圆形中移动的角度
                    mStartArc = mStartArc - dDegree * 2 / 60;
                    if (mStartArc < 0) {
                        mStartArc += 24;
                    } else if (mStartArc >= 24) {
                        mStartArc -= 24;
                    }
                    invalidate();
                } else if (mTouchFlag == 1) {

                    mEndArc = (mEndArc * TIME_UNTI - dDegree) / TIME_UNTI;
                    if (mEndArc < 0) {
                        mEndArc += 24;
                    } else if (mEndArc >= 24) {
                        mEndArc -= 24;
                    }
                    invalidate();
                }
                if (mTimeCallBackListener != null) {
                    mTimeCallBackListener.timeTo(calculateHourAndMinute(mStartArc), calculateHourAndMinute(mEndArc), isOpen);
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 返回当前浮点类型表示的时间点，12个钟头为一个单位
     * 因为浮点类型或有细微分差，这里转到以5分钟单位的分数
     *
     * @param time
     * @return
     */
    private int[] calculateHourAndMinute(float time) {
        int[] times = new int[2];
        times[0] = (int) time;
        int minute = (int) ((time - times[0]) * 60);
        int mode = minute % 5;
        if (mode == 0) {
            times[1] = minute;
        } else {
            times[1] = minute - mode + moveUnti;
            if (times[1] == 60) {
                times[1] = 0;
                times[0] += 1;
                if (times[0] < 0) {
                    times[0] += 24;
                } else if (times[0] >= 24) {
                    times[0] -= 24;
                }
            }
        }
        return times;
    }

    /**
     * 计算开始和结束的时间长度
     * 因为浮点类型或有细微分差，这里转到以5分钟单位的分数
     *
     * @param time
     * @return
     */
    private int[] calculateTimeLength(float time) {
        int[] times = new int[2];
        times[0] = (int) time;
        int minute = (int) ((time - times[0]) * 60);
        int mode = minute % 5;
        if (mode == 0) {
            times[1] = minute;
        } else {
            times[1] = minute - mode + moveUnti;
            if (times[1] == 60) {
                times[1] = 0;
                times[0] += 1;
                if (times[0] < 0) {
                    times[0] += 24;
                } else if (times[0] > 24) {
                    times[0] -= 24;
                }
            }
        }
        if (times[0] == 0 && times[1] == 0) {//开始结束时间相等，等于24小时
            times[0] = 24;
        }
        return times;
    }

    private int calculateRadiansFromAngle(float angle) {
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }
        int radians = (int) ((unit * 360) - ((360 / 4) * 3));
        if (radians < 0)
            radians += 360;
//        Logc.d("radians", radians + "");
        return radians;
    }

    /***
     * 绕着圆心旋转angle角度之后的rect坐标点
     * @param bound
     * @param angle
     */
    private Rect rotateAngle(Rect bound, float angle) {
        Point left_top = new Point(bound.left, bound.top);
        Point right_bottom = new Point(bound.right, bound.bottom);

        left_top = rotateAngle(left_top, angle);
        right_bottom = rotateAngle(right_bottom, angle);
        if (left_top.x > right_bottom.x) {
            int p = left_top.x;
            left_top.x = right_bottom.x;
            right_bottom.x = p;
        }
        if (left_top.y > right_bottom.y) {
            int p = left_top.y;
            left_top.y = right_bottom.y;
            right_bottom.y = p;
        }
        return new Rect(left_top.x, left_top.y, right_bottom.x, right_bottom.y);
    }

    /**
     * 绕着圆心旋转angle角度之后的point坐标点
     *
     * @param point
     * @param angle
     */
    private Point rotateAngle(Point point, float angle) {
        //逆时针旋转角度坐标变换公式
        int x = (int) ((point.x - mCx) * Math.cos(angle * (Math.PI * 2 / 360)) - (point.y - mCy) * Math.sin(angle * (Math.PI * 2 / 360)) + mCx);
        int y = (int) ((point.x - mCx) * Math.sin(angle * (Math.PI * 2 / 360)) + (point.y - mCy) * Math.cos(angle * (Math.PI * 2 / 360)) + mCy);
        return new Point(x, y);
    }

    private boolean isPointInRecf(Point p, Rect rect) {
        if (p.x >= rect.left - TOUCH_mWidth && p.x <= rect.right + TOUCH_mWidth && p.y <= rect.bottom + TOUCH_mWidth && p.y >= rect.top - TOUCH_mWidth)
            return true;
        return false;
    }

    @Override
    public void invalidate() {
        //重绘
        super.invalidate();
    }

    @Override
    public void onClick(View view) {

    }

    public void openClock() {
        isOpen = true;
        clockOpen();
        invalidate();
    }

    public void closeClock() {
        isOpen = false;
        clockClose();
        invalidate();
    }

    public void setBigColor(int mBigColor) {
        this.mBigColor = mBigColor;
    }

    public void setSmallColor(int mSmallColor) {
        this.mSmallColor = mSmallColor;
    }

    public void setBigGraduationColor(int mBigGraduationColor) {
        this.mBigGraduationColor = mBigGraduationColor;
    }

    public void setSmallGraduationColor(int mSmallGraduationColor) {
        this.mSmallGraduationColor = mSmallGraduationColor;
    }

    public void setGdtTextColor(int mGdtTextColor) {
        this.mGdtTextColor = mGdtTextColor;
    }

    public void setTimeTextColro(int mTimeTextColro) {
        this.mTimeTextColro = mTimeTextColro;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public void setGdtmWidth(int mGdtmWidth) {
        this.mGdtmWidth = mGdtmWidth;
    }

    public void setTimeWidth(int mTimeWidth) {
        this.mTimeWidth = mTimeWidth;
    }

    public void setGdtTextSize(int mGdtTextSize) {
        this.mGdtTextSize = mGdtTextSize;
    }

    public void setTimeTextSize(int mTimeTextSize) {
        this.mTimeTextSize = mTimeTextSize;
    }

    public void setStartArc(float mStartArc) {
        this.mStartArc = mStartArc;
    }

    public void setEndArc(float mEndArc) {
        this.mEndArc = mEndArc;
    }

    public void setStartAlarmClockDrawable(Drawable mStartAlarmClockDrawable) {
        this.mStartAlarmClockDrawable = mStartAlarmClockDrawable;
    }

    public void setEndAlarmClockDrawable(Drawable mEndAlarmClockDrawable) {
        this.mEndAlarmClockDrawable = mEndAlarmClockDrawable;
    }

    public void setTimeCallBackListener(TimeCallBackListener timeCallBackListener) {
        this.mTimeCallBackListener = timeCallBackListener;
    }

    public interface TimeCallBackListener {
        /**
         * int数组，下标为0是钟头，下标为1是分钟
         *
         * @param startTime
         * @param endTime
         * @param isOpen
         */
        public void timeTo(int[] startTime, int[] endTime, boolean isOpen);
    }
}
