package com.junkchen.chartdemo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CanvasDemo(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val greenPaint = createPaint(Color.GREEN, Paint.Style.STROKE, 4F)
    private val redPaint = createPaint(Color.RED, Paint.Style.STROKE, 4F)
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//        mPaint.isFilterBitmap = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {

        val rect = RectF(0f, 0f, 400f, 200f)
        if (canvas != null) {
//            canvas.drawRect(rect, greenPaint)
//
//            canvas.translate(100f, 100f)
//            canvas.drawRect(rect, redPaint)


//            canvas.drawColor(Color.RED)
//            //保存的画布大小为全屏幕大小
//            canvas.save()
//
//            canvas.clipRect(Rect(100, 100, 800, 800))
//            canvas.drawColor(Color.GREEN)
//            //保存画布大小为Rect(100, 100, 800, 800)
//            canvas.save()
//
//            canvas.clipRect(Rect(200, 200, 700, 700))
//            canvas.drawColor(Color.BLUE)
//            //保存画布大小为Rect(200, 200, 700, 700)
//            canvas.save()
//
//            canvas.clipRect(Rect(300, 300, 600, 600))
//            canvas.drawColor(Color.BLACK)
//            //保存画布大小为Rect(300, 300, 600, 600)
//            canvas.save()
//
//            canvas.clipRect(Rect(400, 400, 500, 500))
//            canvas.drawColor(Color.WHITE)
//
//            //将栈顶的画布状态取出来，作为当前画布，并画成黄色背景
//            canvas.restore()
//            canvas.drawColor(Color.YELLOW)

            val mWidth = 400f
            val mHeight = 400f

            val srcBitmap = makeSrc(mWidth, mHeight)
            val dstBitmap = makeDst(mWidth, mHeight)  // circle

//            canvas.drawColor(Color.GREEN)
            canvas.translate(10f, 10f)

            val sc: Int = canvas.saveLayer(0f, 0f, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG)
            canvas.drawBitmap(dstBitmap, 0f, 0f, mPaint)
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(srcBitmap, 0f, 0f, mPaint)
            mPaint.xfermode = null
            canvas.restoreToCount(sc)
        }
    }

    private fun createPaint(color: Int, style: Paint.Style, width: Float): Paint {
        val paint = Paint()
        paint.color = color
        paint.style = style
        paint.strokeWidth = width
        paint.isAntiAlias = true
        paint.isDither = false
        return paint
    }

    private fun makeDst(width: Float, height: Float): Bitmap {
        val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFFFFCC44.toInt()
        c.drawOval(RectF(0f, 0f, width * 3 / 4, height * 3 / 4), p)
        return b
    }

    private fun makeSrc(width: Float, height: Float): Bitmap {
        val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = 0xFF66AAFF.toInt()
        c.drawRect(width / 3, height / 3, width * 19 / 20, height * 19 / 20, p)
        return b
    }
}