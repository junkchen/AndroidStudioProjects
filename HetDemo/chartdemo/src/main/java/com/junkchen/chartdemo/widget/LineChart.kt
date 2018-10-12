package com.junkchen.chartdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * 折线图
 * Create by Junk Chen on 2018/9/29.
 */
class LineChart(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    data class LineData<T>(val x: Float, val y: Float, val data: T)

    private val linePaint = Paint()
    private val axisPaint = Paint()
    private val textPaint = Paint()

    @ColorInt
    var lineColor: Int = Color.BLUE
    @ColorInt
    var axisValueColor: Int = Color.DKGRAY
    @ColorInt
    var axisGridColor: Int = Color.DKGRAY
    @ColorInt
    var highlighterBackgroundColor: Int = Color.BLUE
    @ColorInt
    var highlighterTextColor: Int = Color.WHITE

    var highlighterTextSize: Float = 16F
    var axisTextSize: Float = 12F
//        get() = convertSp2Px(field)
        set(value) {
            field = value
            postInvalidate()
        }

    private fun convertSp2Px(value: Float): Float {
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics)
    }

    var lineDataSet: ArrayList<LineData<String>> = ArrayList()
        get() {
            if (field.isEmpty()) {
                // 7
                val random = Random()
                field.add(LineData(420F, (random.nextInt(1000) + 200).toFloat(),
                        "7:00"))
                field.add(LineData(500F, (random.nextInt(1000) + 200).toFloat(),
                        "8:20"))
                field.add(LineData(600F, (random.nextInt(1000) + 200).toFloat(),
                        "10:00"))
                field.add(LineData(870F, (random.nextInt(1000) + 200).toFloat(),
                        "14:30"))
                field.add(LineData(1110F, (random.nextInt(1000) + 200).toFloat(),
                        "18:30"))
            }
            return field
        }
        set(value) {
            field = value
            postInvalidate()
        }

    private val points = ArrayList<PointF>()
    var startTime: Int = 7
    var endTime: Int = 19
    private var xAxisLineCount = 0
        get() = (endTime - startTime) / 2
    private var xInterval = 0F
    private var maxY: Float = 1200F

    init {
        linePaint.color = lineColor
        linePaint.strokeWidth = 4F
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.isAntiAlias = true
        linePaint.isDither = false

        axisPaint.color = Color.DKGRAY
        axisPaint.strokeWidth = 1F

        textPaint.color = axisValueColor
        textPaint.textSize = 18F
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawColor(Color.parseColor("#D9E5E8"))

            val xAxisSpace = (width - paddingLeft - paddingRight).toFloat() / xAxisLineCount

            // 图表边框范围
            val chartBound = RectF(paddingLeft.toFloat(), paddingTop.toFloat(),
                    (width - paddingRight).toFloat(), (height - paddingBottom).toFloat())
            val chartWidth = chartBound.width()
            val chartHeight = chartBound.height()

            // 绘制 x 坐标轴值
            textPaint.color = axisValueColor
            textPaint.textSize = convertSp2Px(axisTextSize)
            for (i in 0..xAxisLineCount) {
                val time = startTime + i * 2
                when (i) {
                    0 -> textPaint.textAlign = Paint.Align.LEFT
                    xAxisLineCount -> textPaint.textAlign = Paint.Align.RIGHT
                    else -> textPaint.textAlign = Paint.Align.CENTER
                }
                canvas.drawText("$time:00", chartBound.left + xAxisSpace * i, chartBound.bottom, textPaint)
            }

            // 绘制 x 轴网格线
            val textBound = Rect()  // 文本范围
            textPaint.getTextBounds("8:00", 0, 4, textBound)
            val xAxisGridBottom = chartHeight - textBound.height()
            val lineHeight = xAxisGridBottom
            axisPaint.color = axisGridColor
            for (i in 0..xAxisLineCount) {
                val xStart = xAxisSpace * i + paddingLeft
                canvas.drawLine(xStart, paddingTop.toFloat(), xStart, xAxisGridBottom, axisPaint)
            }

            // 先造点儿数据
            points.clear()
            val random = Random()
            val timeLength = (endTime - startTime) * 60
            xInterval = chartWidth / timeLength
            if (lineDataSet != null) {
                lineDataSet.forEach {
                    points.add(PointF((it.x - 420) * xInterval + paddingLeft, it.y / maxY * lineHeight))
                }
            } else {
                for (i in 0..5) {
                    points.add(PointF(i * i * chartWidth / 25 + paddingLeft,
                            (random.nextInt(800) + 200) / maxY * lineHeight))
                }
            }

            // 绘制折线
            val path = Path()
            path.reset()
            for ((i, point) in points.withIndex()) {
                when (i) {
                    0 -> path.moveTo(point.x, point.y)
                    else -> path.lineTo(point.x, point.y)
                }
            }
            linePaint.style = Paint.Style.STROKE
            linePaint.color = lineColor
            canvas.drawPath(path, linePaint)

            // 绘制圆点
            linePaint.style = Paint.Style.FILL
            points.forEach {
                canvas.drawCircle(it.x, it.y, 12F, linePaint)
            }
            linePaint.style = Paint.Style.FILL
            linePaint.color = Color.WHITE
            points.forEach {
                canvas.drawCircle(it.x, it.y, 8F, linePaint)
            }

            /**
             * Set highlighter for selected point
             */
            // 绘制高亮值 假设现在选中第三个点
//            val pointF = points[random.nextInt(5)]
            val pointF = points[highlighterIndex]
            val selectedX = pointF.x
            val selectedY = pointF.y
//            val timeStr = "time: 10/9 19:30"
            val timeStr = "time: ${lineDataSet[highlighterIndex].data}"
            val valueStr = "water: ${selectedY.toInt()}ml"
            textPaint.textSize = convertSp2Px(highlighterTextSize)
            textPaint.getTextBounds(timeStr, 0, timeStr.length, textBound)
            var highlighterTextWidth = textBound.width()
            textPaint.getTextBounds(valueStr, 0, valueStr.length, textBound)
            if (highlighterTextWidth < textBound.width()) {
                highlighterTextWidth = textBound.width()
            }
            // Highlighter width and height
            val highlighterPadding = 16F
            val highlighterPointMargin = 24
            val highlighterWidth = highlighterTextWidth + highlighterPadding * 2
            val highlighterHeight = textBound.height() * 2 + highlighterPadding * 3
            // Highlighter bound
            var highlighterLeft = selectedX - highlighterWidth / 2F  // left
            var highlighterTop = selectedY - highlighterHeight - highlighterPointMargin  // top
            var highlighterRight = selectedX + highlighterWidth / 2F  // right
            var highlighterBottom = selectedY - highlighterPointMargin // bottom
            if (highlighterLeft < chartBound.left) {
                highlighterLeft = chartBound.left
                highlighterRight = highlighterLeft + highlighterWidth
            }
            if (highlighterRight > chartBound.right) {
                highlighterRight = chartBound.right
                highlighterLeft = highlighterRight - highlighterWidth
            }
            if (highlighterTop < chartBound.top) {
                highlighterTop = selectedY + highlighterPointMargin
                highlighterBottom = highlighterTop + highlighterHeight
            }

            // Highlighter bound
            val highlighterRectF = RectF(highlighterLeft, highlighterTop,
                    highlighterRight, highlighterBottom)

//            linePaint.color = Color.parseColor("#AA0000FF")
            linePaint.color = highlighterBackgroundColor
            val highlighterRadius = 8F
            canvas.drawRoundRect(highlighterRectF, highlighterRadius, highlighterRadius, linePaint)
            with(path) {
                reset()
                val triangleXMarginHalf = 12
                var triangleLeftX = selectedX - triangleXMarginHalf
                var triangleRightX = selectedX + triangleXMarginHalf
                if (triangleLeftX < highlighterLeft + highlighterRadius) {
                    triangleLeftX = highlighterLeft + highlighterRadius
                    triangleRightX = triangleLeftX + triangleXMarginHalf * 2
                }
                if (triangleRightX > highlighterRight - highlighterRadius) {
                    triangleRightX = highlighterRight - highlighterRadius
                    triangleLeftX = triangleRightX - triangleXMarginHalf * 2
                }
                if (highlighterTop < selectedY) {  // 上面显示不下了
                    moveTo(triangleLeftX, highlighterBottom)
                    lineTo(triangleRightX, highlighterBottom)
                    lineTo(selectedX, highlighterBottom + highlighterPointMargin / 3 * 2)
                } else {
                    moveTo(triangleLeftX, highlighterTop)
                    lineTo(triangleRightX, highlighterTop)
                    lineTo(selectedX, highlighterTop - highlighterPointMargin / 3 * 2)
                }
                close()
            }
            canvas.drawPath(path, linePaint)
            // Draw highlighter text
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.color = highlighterTextColor
            canvas.drawText(timeStr,
                    highlighterLeft + (highlighterRight - highlighterLeft) / 2,
                    highlighterTop + textBound.height() + highlighterPadding,
                    textPaint)
            canvas.drawText(valueStr,
                    highlighterLeft + (highlighterRight - highlighterLeft) / 2,
                    highlighterBottom - highlighterPadding * 1.2F,
                    textPaint)
        }
    }

    private var lastTouchX = 0F
    private var highlighterIndex = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (points.isEmpty()) {
            return super.onTouchEvent(event)
        }
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val downX = event.x
                    lastTouchX = downX
                    touchHandle(downX)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveX = event.x
                    if (Math.abs(moveX - lastTouchX) > xInterval / 2) {
                        lastTouchX = moveX
                        touchHandle(moveX)
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {

                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun touchHandle(x: Float) {
        val tagPosition = findTagPosition(x)
        if (highlighterIndex != tagPosition) {
            highlighterIndex = tagPosition
            postInvalidate()
        }
    }

    /**
     * Find tag index position by touch x coordinate.
     *
     * @param x touch x coordinate
     * @return tag index
     */
    private fun findTagPosition(x: Float): Int {
        var newTagIndex = 0
        for (i in 0 until points.size) {
            val point = points[i]
            if (x <= point.x) {
                newTagIndex = if (i == 0) {
                    0
                } else {
                    val middle = (point.x + points[i - 1].x) / 2
                    if (x < middle) {
                        i - 1
                    } else {
                        i
                    }
                }
                break
            }
        }
        return newTagIndex
    }
}