package com.junkchen.chartdemo

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.junkchen.chartdemo.widget.BarChart

import kotlinx.android.synthetic.main.activity_bar_chart_test.*
import kotlinx.android.synthetic.main.content_bar_chart_test.*
import java.util.*

class BarChartTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart_test)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        barChart.apply {
            setPadding(16, 8, 16, 8)
            val data = arrayListOf<BarChart.BarDataItem>()
            val random = Random()
            for (i in 0..20) {
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
    }

}
