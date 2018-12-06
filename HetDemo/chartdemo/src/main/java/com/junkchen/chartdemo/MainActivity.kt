package com.junkchen.chartdemo

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.junkchen.chartdemo.widget.BarChart
import com.junkchen.chartdemo.widget.LineData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setContentView(WaterPieChart(this))
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
            setPadding(4, 0, 4, 0)
            val data = arrayListOf<BarChart.BarDataItem<String>>()
            val random = Random()
            for (i in 0..9) {
                data.add(BarChart.BarDataItem(1.0, random.nextDouble() * 1000, "#$i"))
            }
            this.barDataSet = data
        }
        setContentView(barChart)
    }

}
