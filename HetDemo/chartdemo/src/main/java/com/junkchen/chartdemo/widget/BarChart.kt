package com.junkchen.chartdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View

/**
 * 柱状图
 * Created by Junk Chen on 2018/11/05.
 */
class BarChart(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    data class BarDataItem<T>(val x: Double, val y: Double, val data: T)

    private val displayMetrics = DisplayMetrics()

    var yMax = 0.0
    var barDataSet: MutableList<BarDataItem<String>> = arrayListOf()
        set(value) {
            field = value
            postInvalidate()
        }

    val barPaint = Paint().apply {
        this.style = Paint.Style.FILL
        this.isAntiAlias = true
        this.color = Color.RED
    }

    val axisPaint = Paint().apply {
        this.style = Paint.Style.FILL
        this.isAntiAlias = true
        this.color = Color.BLACK
    }

    val textPaint = Paint().apply {
        this.style = Paint.Style.FILL
        this.isAntiAlias = true
        this.color = Color.BLACK
        this.textAlign = Paint.Align.CENTER
    }

    /**
     * x 坐标轴高度
     */
    var xAxisHeight = 16f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * x 坐标轴文字大小
     */
    var axisTextSize = 12f
        set(value) {
            field = value
            postInvalidate()
        }

    init {

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        display.getMetrics(displayMetrics)
        canvas?.apply {
            // 图表边框范围
            val chartBound = Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
            val chartWidth = chartBound.width()
            val chartHeight = chartBound.height()
            // Bar bound
            val barBound = Rect(paddingLeft, paddingTop, width - paddingRight,
                    (chartHeight - convertValueToPx(xAxisHeight)).toInt())
            // X axis bound
            val xAxisBound = Rect(paddingLeft, barBound.bottom, width - paddingRight, chartHeight)

            // 1. draw background

            val dataSize = barDataSet.size
            val barItemWidth = chartWidth * 1.0f / dataSize

            // 2. draw x axis value
            var xPos = barItemWidth / 2
            textPaint.textSize = convertValueToPx(axisTextSize, TypedValue.COMPLEX_UNIT_SP)
            val fm = textPaint.fontMetrics
            val yPos = xAxisBound.centerY() + (fm.bottom - fm.top) / 2 - fm.bottom
            barDataSet.forEach {
                this.drawText(it.data, xPos, yPos, textPaint)
                xPos += barItemWidth
                if (yMax < it.y) {
                    yMax = it.y
                }
            }

            // 3. draw axis

            // 4. draw bar
            val rx = 4f
            val ry = 4f
            val rxPx = convertValueToPx(rx)
            val ryPx = convertValueToPx(ry)
            yMax *= 1.1
            val space = barItemWidth * 0.15f
            barDataSet.forEachIndexed { index, barDataItem ->
                val yValue = barDataItem.y
                val rect = RectF().apply {
                    left = barBound.left + barItemWidth * index + space
                    top = ((1 - yValue / yMax) * barBound.height()).toFloat()
                    right = barItemWidth * (index + 1) - space
                    bottom = barBound.bottom.toFloat()
                }
                this.drawRoundRect(rect, rxPx, ryPx, barPaint)
                this.drawText(yValue.toInt().toString(), rect.centerX(), rect.top - 4, textPaint)
            }
        }
    }

    private fun convertValueToPx(value: Float, unit: Int = TypedValue.COMPLEX_UNIT_DIP): Float =
            when (unit) {
                TypedValue.COMPLEX_UNIT_DIP -> {
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)
                }
                TypedValue.COMPLEX_UNIT_SP -> {
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, displayMetrics)
                }
                else -> value
            }
}