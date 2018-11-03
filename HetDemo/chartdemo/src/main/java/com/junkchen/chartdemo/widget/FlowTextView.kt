package com.junkchen.chartdemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import com.junkchen.chartdemo.R

/**
 * Create by Junk Chen on 2018/11/03.
 */
class FlowTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val TAG = "FlowTextView"

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val dividerPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    @Dimension(unit = Dimension.SP)
    var textSize: Float = 12.0f
        set(value) {
//            field = convertSp2Px(value)
            field = value
            postInvalidate()
        }

    @ColorInt
    var textColor: Int = Color.BLACK

    @Dimension(unit = Dimension.DP)
    var dividerSize: Float = 1.0F

    @ColorInt
    var dividerColor: Int = Color.GRAY

    /**
     * 行文本高度
     */
    @Dimension(unit = Dimension.DP)
    var rowTextHeight: Float = 48F

    /**
     * 两个文本之间的间距
     */
    @Dimension(unit = Dimension.DP)
    var textInterval = 48F

    var textList: List<String> = arrayListOf("Google", "Kotlin", "Android", "IOS", "Python", "Apple")
        set(value) {
            field = value
            postInvalidate()
        }

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FlowTextView,
                0, 0).apply {
            try {
                textSize = getDimension(R.styleable.FlowTextView_textSize, 12F)
                textColor = getColor(R.styleable.FlowTextView_textColor, Color.BLACK)
                dividerSize = getDimension(R.styleable.FlowTextView_dividerSize, 1F)
                dividerColor = getColor(R.styleable.FlowTextView_dividerColor, Color.GRAY)
                rowTextHeight = getDimension(R.styleable.FlowTextView_rowTextHeight, 48F)
                textInterval = getDimension(R.styleable.FlowTextView_textInterval, 18F)
            } finally {
                recycle()
            }
        }

        textPaint.textSize = textSize
        textPaint.color = textColor

        dividerPaint.strokeWidth = dividerSize
        dividerPaint.color = dividerColor
    }

    /**
     * 保存每行要显示的文本
     */
    val mRowTextList: List<List<String>> = arrayListOf()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = rowTextHeight.toInt()

        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)

//        val minh: Int = View.MeasureSpec.getSize(w) - mTextWidth.toInt() + paddingBottom + paddingTop
//        val h: Int = View.resolveSizeAndState(
//                View.MeasureSpec.getSize(w) - mTextWidth.toInt(),
//                heightMeasureSpec,
//                0
//        )

        var hc = 1
        var tmpWidth = paddingLeft
        for (text in textList) {
            val tw = textPaint.measureText(text)
            if (tmpWidth + tw + paddingRight > widthSize) {
                hc++
                tmpWidth = paddingLeft
            }
            tmpWidth += tw.toInt() + textInterval.toInt()
        }
        setMeasuredDimension(widthSize, height * hc)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            // 每行文本起始位置
            var rowTextStartX = paddingLeft.toFloat()
            var currentHeight = 0F
            val bounds = Rect()
            val fm = textPaint.fontMetrics
            textList.forEach {
                textPaint.getTextBounds(it, 0, it.length, bounds)
                if (rowTextStartX + bounds.width() + paddingRight > width) {
                    rowTextStartX = paddingLeft.toFloat()
                    currentHeight += rowTextHeight
                    drawLine(paddingLeft.toFloat(), currentHeight,
                            width.toFloat(), currentHeight, dividerPaint)
                }

                val textY = currentHeight + rowTextHeight / 2 + (fm.bottom - fm.top) / 2 - fm.bottom
                drawText(it, rowTextStartX.toFloat(), textY, textPaint)
                rowTextStartX += bounds.width() + textInterval
            }
            drawLine(0F,
                    currentHeight + rowTextHeight - dividerSize / 2,
                    width.toFloat(),
                    currentHeight + rowTextHeight - dividerSize / 2,
                    dividerPaint)
        }
    }

    private fun convertSp2Px(value: Float): Float {
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics)
    }
}