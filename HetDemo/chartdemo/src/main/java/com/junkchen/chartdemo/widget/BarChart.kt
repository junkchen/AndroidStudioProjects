package com.junkchen.chartdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * 柱状图
 * Created by Junk Chen on 2018/11/05.
 */
class BarChart(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(
        context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    companion object {
        val TAG by lazy { BarChart::class.java.canonicalName }
    }

    //    data class BarDataItem<T>(val x: Double, val y: Double, val data: T)
    data class BarDataItem(val x: Float, val y: Float,
                           val xLabel: String = x.toString(),
                           val yLabel: String = y.toString(),
                           val yValue: String = y.toString())

    class LimitLine(val limit: Float,
                    val label: String = limit.toString(),
                    val lineColor: Int = Color.RED,
                    val textColor: Int = Color.BLACK,
                    var pathEffect: PathEffect? = null)

    private val displayMetrics = DisplayMetrics()
    private var onBarItemClickedListener: OnBarItemClickedListener? = null

    var yMax = 0f
    var barDataSet: MutableList<BarDataItem> = arrayListOf()
        set(value) {
            field = value
            postInvalidate()
        }
    private var barItemWidth = 0f

    val limitLines: MutableList<LimitLine> = arrayListOf()

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

    val limitLinePaint = Paint().apply {
        this.style = Paint.Style.FILL
        this.isAntiAlias = true
        this.textAlign = Paint.Align.RIGHT
        this.color = Color.WHITE
        this.strokeWidth = 2f
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

    var barColor = Color.parseColor("#ff3d00")
    var barValueColor = Color.parseColor("#424242")
    var barBoundBgColor = Color.parseColor("#9e9e9e")
    var xAxisBoundBgColor = Color.parseColor("#bdbdbd")

    /**
     * limit 线文字大小
     */
    var limitLineTextSize = axisTextSize

    /**
     * 是否绘制 bar 的 y 值
     */
    var drawBarValueEnabled = true

    /**
     * bar 值的文字大小
     */
    var barValueTextSize = 12f
        set(value) {
            field = value
            postInvalidate()
        }

    /**
     * 柱状图 Bar 的圆角弧度
     */
    var radiusX = 4f
    var radiusY = 4f

    /**
     * 高亮 bar 的索引 index
     */
    var highlightIndex = 0

    /**
     * 是否高亮显示
     */
    var highlightEnabled = true
        set(value) {
            field = value
            highlightIndex = if (value) 0 else -1
        }

    /**
     * 高亮显示
     */
    var highlightBarColor: Int = Color.parseColor("#c30000")
    var highlightTextColor: Int = Color.parseColor("#c30000")

    /**
     * 屏幕宽度可见的 bar 数量，0表示自适应显示所有数据
     */
    var visibleBarSize = 0f

    /**
     * 初始化加载
     */
    private var isInitialLoad = true

    /**
     * 无数据时显示的文本
     */
    var noDataText: String? = "No Data!"

    /**
     * 无数据文本文字颜色
     */
    var noDataTextColor: Int = Color.BLACK

    /**
     * 无数据文本文字大小
     */
    var noDataTextSize: Float = 14f

    /**
     * 上次触摸的 x 的位置
     */
    private var lastTouchX = 0f

    private var downX = 0f
    private var moveX = 0f
    private var lastMoveX = 0f

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
//        lastMoveX = paddingLeft.toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        display.getMetrics(displayMetrics)
        if (barDataSet.isEmpty()) {
            textPaint.textSize = convertValueToPx(noDataTextSize, TypedValue.COMPLEX_UNIT_SP)
            textPaint.color = noDataTextColor
            noDataText?.let { canvas?.drawText(it, width / 2f, height / 2f, textPaint) }
            return
        }
        canvas?.apply {
            this.drawColor(Color.WHITE)
            Log.i(TAG, "1. width: $width, height: $height")

            // 图表边框范围
            val clipRect = Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
            this.clipRect(clipRect)

            val dataSize = barDataSet.size
            barItemWidth = if (visibleBarSize == 0f) {
                clipRect.width() * 1.0f / dataSize
            } else {
                clipRect.width() * 1.0f / visibleBarSize
            }
            val chartWidth = barItemWidth * dataSize

            val chartBound = Rect(0, paddingTop, chartWidth.toInt(), height - paddingBottom)

            // Bar bound
            val barBound = Rect().apply {
                left = chartBound.left
                top = chartBound.top
                right = chartBound.right
                bottom = (chartBound.bottom - convertValueToPx(xAxisHeight)).toInt()
            }

//            val chartWidth = chartBound.width()
            val chartHeight = chartBound.height()
            Log.i(TAG, "2. chartWidth: $chartWidth, chartHeight: $chartHeight")
            val matrix = Matrix()
//            matrix.postScale(1.5f, 1f)
            val maxMoveX = chartBound.width() - width + paddingLeft // + paddingRight
            lastMoveX += moveX
            if (lastMoveX >= paddingLeft) {
                lastMoveX = paddingLeft.toFloat()
            } else if (lastMoveX <= -maxMoveX) {
                lastMoveX = (-maxMoveX).toFloat()
            }
            if (isInitialLoad) {
                isInitialLoad = false
                lastMoveX = if (highlightIndex == dataSize - 1) {
                    (-maxMoveX).toFloat()
                } else {
                    paddingLeft.toFloat()
                }
            }
            Log.i(TAG, "lastMoveX: $lastMoveX")
            matrix.setTranslate(lastMoveX, 0f)
            this.save()
            this.concat(matrix)

            // X axis bound
            val xAxisBound = Rect().apply {
                left = chartBound.left
                top = barBound.bottom
                right = chartBound.right
                bottom = chartBound.bottom
            }

            // 1. draw background
//            barPaint.color = Color.parseColor("#77654321")
//            this.drawRect(chartBound, barPaint)
            barPaint.color = barBoundBgColor
            this.drawRect(barBound, barPaint)
            barPaint.color = xAxisBoundBgColor
            this.drawRect(xAxisBound, barPaint)

            // 2. draw x axis value
            var xPos = barItemWidth / 2
            textPaint.textSize = convertValueToPx(axisTextSize, TypedValue.COMPLEX_UNIT_SP)
            val fm = textPaint.fontMetrics
            yMax = 0f
            val yPos = xAxisBound.centerY() + (fm.bottom - fm.top) / 2 - fm.bottom
            textPaint.color = Color.BLACK
            textPaint.textAlign = Paint.Align.CENTER
            barDataSet.forEachIndexed { index, dataItem ->
                if (!highlightEnabled || index != highlightIndex) {
                    this.drawText(dataItem.xLabel, xPos, yPos, textPaint)
                }
                xPos += barItemWidth
                if (yMax < dataItem.y) {
                    yMax = dataItem.y
                }
            }

            limitLines.forEach {
                if (yMax < it.limit) {
                    yMax = it.limit
                }
            }

            // 3. draw axis

            // 4. draw bar
            val rxPx = convertValueToPx(radiusX)
            val ryPx = convertValueToPx(radiusY)
            yMax *= 1.08f
            val space = barItemWidth * 0.191f
            barPaint.color = barColor
            textPaint.color = barValueColor
            textPaint.textSize = convertValueToPx(barValueTextSize, TypedValue.COMPLEX_UNIT_SP)
            val roundRect = RectF()
            val rect = RectF()
            barDataSet.forEachIndexed { index, barDataItem ->
                if (!highlightEnabled || index != highlightIndex) {
                    val yValue = barDataItem.y
                    roundRect.apply {
                        // left = barBound.left + barItemWidth * index + space
                        left = barItemWidth * index + space
                        top = barBound.bottom - yValue / yMax * barBound.height()
                        right = barItemWidth * (index + 1) - space
                        bottom = barBound.bottom.toFloat()
                    }
                    rect.set(roundRect)
                    rect.top = if (roundRect.height() <= ryPx * 2) {
                        roundRect.bottom - roundRect.height() / 2
                    } else {
                        roundRect.bottom - ryPx
                    }
                    this.drawRoundRect(roundRect, rxPx, ryPx, barPaint)
                    this.drawRect(rect, barPaint)
                    if (drawBarValueEnabled) {
                        this.drawText(barDataItem.yValue, roundRect.centerX(), roundRect.top - 4, textPaint)
                    }
                }
            }

            // 5. Highlighter
            if (highlightEnabled) {
                // Bar
                val highlightBar = barDataSet[highlightIndex]
                val yValue = highlightBar.y
                roundRect.apply {
                    //                    left = barBound.left + barItemWidth * highlightIndex + space
                    left = barItemWidth * highlightIndex + space
                    top = barBound.bottom - yValue / yMax * barBound.height()
                    right = barItemWidth * (highlightIndex + 1) - space
                    bottom = barBound.bottom.toFloat()
                }
                rect.set(roundRect)
                rect.top = if (roundRect.height() <= ryPx * 2) {
                    roundRect.bottom - roundRect.height() / 2
                } else {
                    roundRect.bottom - ryPx
                }
                barPaint.color = highlightBarColor
                this.drawRoundRect(roundRect, rxPx, ryPx, barPaint)
                this.drawRect(rect, barPaint)
                if (drawBarValueEnabled) {
                    this.drawText(highlightBar.yValue, roundRect.centerX(), roundRect.top - 4, textPaint)
                }

                // x axis text
                textPaint.color = highlightTextColor
                xPos = barItemWidth / 2 + barItemWidth * highlightIndex
                this.drawText(highlightBar.xLabel, xPos, yPos, textPaint)
            }
            this.restore()

            // Limit line
            textPaint.textAlign = Paint.Align.RIGHT
            textPaint.textSize = convertValueToPx(limitLineTextSize, TypedValue.COMPLEX_UNIT_SP)
            limitLines.forEach {
                val y = barBound.bottom - it.limit / yMax * barBound.height()
                limitLinePaint.color = it.lineColor
                limitLinePaint.pathEffect = it.pathEffect
                this.drawLine(paddingLeft.toFloat(), y, (width - paddingRight).toFloat(), y, limitLinePaint)
                textPaint.color = it.textColor
                this.drawText(it.label, (width - paddingRight - 16).toFloat(), y - 8, textPaint)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent: ")
        if (barDataSet.isEmpty()) return super.onTouchEvent(event)
        return gestureDetector.onTouchEvent(event)
    }

    private fun touchHandle(x: Float) {
        val tagPosition = findTagPosition(x)
        if (highlightIndex != tagPosition) {
            highlightIndex = tagPosition
            postInvalidate()
            onBarItemClickedListener?.onBarItemClicked(tagPosition, barDataSet[tagPosition])
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
        val offsetX = x - lastMoveX
        for (i in 0 until barDataSet.size) {
            if (offsetX in (i * barItemWidth)..((i + 1) * barItemWidth)) {
                newTagIndex = i
                break
            }
        }
        return newTagIndex
    }

    interface OnBarItemClickedListener {
        fun onBarItemClicked(position: Int, barDataItem: BarDataItem)
    }

    fun setOnBarItemClickedListener(l: OnBarItemClickedListener?) {
        onBarItemClickedListener = l
    }

    inner class ChartTouchListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            Log.i(TAG, "onDown: ")
            return true
//            return super.onDown(e)
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.i(TAG, "onSingleTapUp: ")
            e?.let {
                // 如果是点击，需要清零移动距离
                moveX = 0f
                touchHandle(it.x)
                lastTouchX = it.x
                return true
            }
            return super.onSingleTapUp(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            Log.i(TAG, "onScroll: ")
//            moveX = this.x - lastTouchX
            moveX = -distanceX
            e2?.let { lastTouchX = it.x }
            postInvalidate()
            return true
//            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.i(TAG, "onFling: ")
//            e2?.let {
//                moveX = it.x - lastTouchX
//                lastTouchX = it.x
//            }
//            postInvalidate()
//            return true
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    private val chartTouchListener = ChartTouchListener()
    private val gestureDetector = GestureDetector(context, chartTouchListener)
}