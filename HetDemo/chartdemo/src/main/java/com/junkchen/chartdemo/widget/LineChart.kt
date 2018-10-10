package com.junkchen.chartdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

fun main(args: Array<String>) {
    for (i in 7 until 19 + 2 step 2) {
        println(i)
    }
//    for (i in 0..((7 - 19) / 2 + 1)) {
//        println("i = $i")
//    }
    for (i in 0..5) {
        println("i = $i")
    }
}

/**
 * 折线图
 * Create by Junk Chen on 2018/9/29.
 */
class LineChart(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr
) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val linePaint = Paint()
    private val axisPaint = Paint()
    private val textPaint = Paint()

    private var startTime: Int = 7
    private var endTime: Int = 19

    init {
        linePaint.color = Color.BLUE
        linePaint.strokeWidth = 4F
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.isAntiAlias = true
        linePaint.isDither = false

        axisPaint.color = Color.DKGRAY
        axisPaint.strokeWidth = 4F

        textPaint.color = Color.DKGRAY
        textPaint.textSize = 18F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 偶数整点时间轴线
        // 奇数整点时间轴线
        // 半小时时间轴线

        if (canvas != null) {
            canvas.drawColor(Color.parseColor("#D9E5E8"))

            val xAxisLineCount = (endTime - startTime) / 2
            val xAxisSpace = width / xAxisLineCount
            for (i in 0..xAxisLineCount) {
                canvas.drawLine((i * xAxisSpace).toFloat(), 0f,
                        (i * xAxisSpace).toFloat(), height.toFloat() - 20,
                        axisPaint)
            }

            // 绘制 x 轴坐标值
            textPaint.color = Color.DKGRAY
            textPaint.textSize = 28F
            for (i in 0..xAxisLineCount) {
                val time = startTime + i * 2
                when (i) {
                    0 -> textPaint.textAlign = Paint.Align.LEFT
                    xAxisLineCount -> textPaint.textAlign = Paint.Align.RIGHT
                    else -> textPaint.textAlign = Paint.Align.CENTER
                }
                canvas.drawText("$time:00", (i * xAxisSpace).toFloat(), (height - 0).toFloat(), textPaint)
            }

            // 先造点儿数据
            val points = ArrayList<PointF>()
            val random = Random()
            for (i in 1..5) {
                points.add(PointF((i * i * width / 30).toFloat(),
                        (random.nextInt(800).toFloat() + 200) / 1000 * height))
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
            linePaint.color = Color.BLUE
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

            // 绘制高亮值 假设现在选中第三个点
            val pointF = points[2]
            val selectedX = pointF.x
            val selectedY = pointF.y
            linePaint.color = Color.parseColor("#0000FF")
            canvas.drawRoundRect(RectF(selectedX - 110, selectedY - 150, selectedX + 110, selectedY - 30), 8F, 8F, linePaint)
            with(path) {
                reset()
                moveTo(selectedX - 20, selectedY - 30)
                lineTo(selectedX + 20, selectedY - 30)
                lineTo(selectedX, selectedY - 16)
                close()
            }
            canvas.drawPath(path, linePaint)
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.color = Color.WHITE
            canvas.drawText("time: 10/9 19:30", selectedX, selectedY  - 100, textPaint)
            canvas.drawText("water: 800ml", selectedX, selectedY - 60, textPaint)
        }
    }
}