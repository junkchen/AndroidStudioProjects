package com.junkchen.chartdemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 折线图
 * Create by Junk Chen on 2018/9/29.
 */
class LineChart(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
        context, attrs, defStyleAttr
) {

    private val linePaint = Paint()
    private val axisPaint = Paint()
    private val textPaint = Paint()

    init {
        linePaint.color = Color.BLUE

        axisPaint.color = Color.DKGRAY
        axisPaint.strokeWidth = 4F

        textPaint.color = Color.DKGRAY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 偶数整点时间轴线

        // 奇数整点时间轴线

        // 半小时时间轴线

    }
}