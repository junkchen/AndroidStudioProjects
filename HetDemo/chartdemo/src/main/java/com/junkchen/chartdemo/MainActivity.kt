package com.junkchen.chartdemo

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.junkchen.chartdemo.widget.BarChart
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setContentView(WaterPieChart(this))
//        setContentView(CanvasDemo2(this))
        showBarChart()

//        line_chart.lineColor = Color.MAGENTA
//        line_chart.axisValueColor = Color.DKGRAY
//        line_chart.highlighterBackgroundColor = Color.MAGENTA
//
//        val dataSet = ArrayList<LineData<String>>()
//        val random = Random()
//        dataSet.add(LineData(420F, (random.nextInt(1000) + 200).toFloat(),
//                "10/10 7:00"))
//        dataSet.add(LineData(500F, (random.nextInt(1000) + 200).toFloat(),
//                "10/10 8:20"))
//        dataSet.add(LineData(600F, (random.nextInt(1000) + 200).toFloat(),
//                "10/10 10:00"))
//        dataSet.add(LineData(870F, (random.nextInt(1000) + 200).toFloat(),
//                "10/10 14:30"))
//        dataSet.add(LineData(1110F, (random.nextInt(1000) + 200).toFloat(),
//                "10/10 18:30"))
////        line_chart.lineDataSet = dataSet
//
//        // FlowTextView
//        flowTextView.textList = arrayListOf("Google", "Kotlin", "Android", "IOS", "Python", "Apple",
//                "Google", "Kotlin", "Android", "IOS", "Python", "Apple", "IOS", "Python", "Apple")
//        flowTextView.rowTextHeight = 96f
    }

    private fun showBarChart() {
        val barChart = BarChart(this).apply {
            setPadding(16, 8, 16, 8)
            val data = arrayListOf<BarChart.BarDataItem>()
            val random = Random()
            for (i in 0..7) {
                val yValue = random.nextFloat() * 1000
                data.add(BarChart.BarDataItem(i.toFloat(), yValue, "#$i", yValue = yValue.toInt().toString()))
            }
            data.add(BarChart.BarDataItem(8f, 8f, "#8"))
            data.add(BarChart.BarDataItem(9f, 4f, "#9"))
            data.add(BarChart.BarDataItem(10f, 18f, "#10"))
            data.add(BarChart.BarDataItem(11f, 1000f, "#11"))
            this.visibleBarSize = 7.5f
            this.highlightIndex = data.size - 1
//            this.highlightEnabled = false
            this.limitLines.add(BarChart.LimitLine(300f, lineColor = Color.YELLOW))
            this.limitLines.add(BarChart.LimitLine(800f, "800优秀",
                    pathEffect = DashPathEffect(floatArrayOf(16f, 8f), 8f)))
            this.barDataSet = data
            this.setOnBarItemClickedListener(object : BarChart.OnBarItemClickedListener {
                override fun onBarItemClicked(position: Int, barDataItem: BarChart.BarDataItem) {
                    Snackbar.make(this@apply, "index: $position, y: ${barDataItem.y}", Snackbar.LENGTH_SHORT).show()
                }
            })
        }
        setContentView(barChart)
    }

}
