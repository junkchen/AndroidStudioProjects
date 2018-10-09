package com.junkchen.chartdemo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * 水量是否达标饼图
 * Create by Junk Chen on 2018/10/08.
 */
class WaterPieChart(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
        context, attrs, defStyleAttr
) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val mPiePaint = Paint()

    init {
        mPiePaint.style = Paint.Style.FILL_AND_STROKE
        mPiePaint.color = Color.RED
        mPiePaint.isAntiAlias = true
        mPiePaint.isDither = false

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension()
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.drawColor(Color.WHITE)

//        val saveLayer: Int = canvas?.saveLayer(
//                0F, 0F, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)!!
//
        val cx = width / 2F
        val cy = height / 2F
//
//        mPiePaint.style = Paint.Style.FILL
//        mPiePaint.color = Color.RED
//        val fillCircleRadius = width / 2F - width / 10F - width / 10F
//        canvas?.drawRect(
//                cx - fillCircleRadius,
//                cy - fillCircleRadius / 2,
//                cx + fillCircleRadius,
//                cy + fillCircleRadius,
//                mPiePaint)
////        canvas?.drawCircle(cx, cy, fillCircleRadius, mPiePaint)
////        canvas?.save()
//
//        mPiePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        mPiePaint.color = Color.BLUE
////        canvas?.drawRect(
////                cx - fillCircleRadius,
////                cy - fillCircleRadius / 2,
////                cx + fillCircleRadius,
////                cy + fillCircleRadius,
////                mPiePaint)
//        canvas?.drawCircle(cx, cy, fillCircleRadius, mPiePaint)
//        mPiePaint.xfermode = null
//        canvas?.restoreToCount(saveLayer)

        val mWidth = width.toFloat()
        val mHeight = height.toFloat()

        val srcBitmap = makeSrc(mWidth, mHeight)
        val dstBitmap = makeDst(mWidth, mHeight)  // circle

//            canvas.drawColor(Color.GREEN)

        val sc: Int = canvas?.saveLayer(0f, 0f, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG)!!
        canvas?.drawBitmap(dstBitmap, 0f, 0f, mPiePaint)
        mPiePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas?.drawBitmap(srcBitmap, 0f, 0f, mPiePaint)
        mPiePaint.xfermode = null
        canvas?.restoreToCount(sc)

        mPiePaint.style = Paint.Style.STROKE
        mPiePaint.strokeWidth = width / 10F
        canvas?.drawCircle(cx, cy, width / 2F - width / 20F, mPiePaint)

    }


    private fun makeDst(width: Float, height: Float): Bitmap {
        val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFFFFCC44.toInt()
        p.color = Color.BLUE
        val cx = width / 2f
        val cy = height / 2f
        val fillCircleRadius = width / 2F - width / 10F - width / 10F
        c.drawRect(cx - fillCircleRadius,
                cy - fillCircleRadius / 2,
                cx + fillCircleRadius,
                cy + fillCircleRadius,
                p)
        return b
    }

    private fun makeSrc(width: Float, height: Float): Bitmap {
        val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFF66AAFF.toInt()
        p.color = Color.RED
        c.drawCircle(width / 2f, height / 2f, width / 2F - width / 10F - width / 10F, p)
        return b
    }
}