package com.junkchen.chartdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CanvasDemo2(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val greenPaint = createPaint(Color.GREEN, Paint.Style.STROKE, 4F)
    private val redPaint = createPaint(Color.RED, Paint.Style.STROKE, 4F)
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.RED
        this.textSize = 36f
    }

    init {
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//        mPaint.isFilterBitmap = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val rect = RectF(0f, 50f, 600f, 200f)
        if (width > rect.width()) {

        } else {

        }
        rect.width()

        canvas?.apply {
            mPaint.color = Color.RED
            this.drawRect(rect, mPaint)
            Log.i(TAG, "1: width=${canvas.width}, height=${canvas.height}")
            this.save()
            val matrix = Matrix()
            matrix.postScale(1.5f, 1f)
//            matrix.postTranslate(-100f, 200f)
            lastMoveX += moveX
//            if (lastMoveX <= -rect.left) {
//                lastMoveX = -rect.left
//            }
//            if (lastMoveX >= canvas.width - rect.right) {
//                lastMoveX = canvas.width - rect.right
//            }
            val space = rect.width() - width
            if (lastMoveX >= 0) {
                lastMoveX = 0f
            } else if (lastMoveX <= -space) {
                lastMoveX = -space
            }
            matrix.setTranslate(lastMoveX, 200f)
            canvas.concat(matrix)
            Log.i(TAG, "matrix: ${matrix.toShortString()}")
//            this.scale(1.5f, 1f)
//            this.scale(1.5f, 1f, 0f, 0f)
//            this.translate(100f, 150f)
            Log.i(TAG, "2: width=${canvas.width}, height=${canvas.height}")
            mPaint.color = Color.BLUE
            this.drawRect(rect, mPaint)
            mPaint.color = Color.RED

            this.drawText("start0123456789", rect.left, rect.bottom, mPaint)
            this.drawText("end0123456789",
                    rect.right - mPaint.measureText("end0123456789"), rect.bottom, mPaint)
            this.restore()
        }
    }

    private var downX = 0f
    private var moveX = 0f
    private var lastMoveX = 0f
    private var upX = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (this.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = this.x
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    moveX = this.x - downX
                    downX = this.x
                    postInvalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    upX = this.x
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
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

    companion object {
        val TAG by lazy { CanvasDemo2::class.java.canonicalName }
    }
}