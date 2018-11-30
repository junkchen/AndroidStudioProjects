package com.junkchen.mpandroidchartdemo


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PieChartFragment : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false)

        this.pieChart = rootView.findViewById(R.id.pieChart)
        setPieChart()
        setPieData()
        return rootView
    }

    private fun setPieChart() {
        pieChart.apply {
            // 是否显示描述
            description.isEnabled = false
            // 透明圆大小
            this.transparentCircleRadius = 0f
            // 设置圆心位置文字大小
            setCenterTextSize(14f)
            // 是否绘制文本标签
            setDrawEntryLabels(false)

            legend.apply {
                this.isEnabled = false
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(true)
                xEntrySpace = 16f
                yEntrySpace = 8f
                yOffset = 0f
            }
        }
    }

    private fun setPieData() {
        // PieEntry list
        val pieEntries = arrayListOf<PieEntry>()
        pieEntries.add(PieEntry(50f, "Android"))
        pieEntries.add(PieEntry(30f, "IOS"))
        pieEntries.add(PieEntry(20f, "Windows"))

        // PieDataSet
        val pieDataSet = PieDataSet(pieEntries, "Mobile")
        val colors = arrayListOf<Int>()
        ColorTemplate.MATERIAL_COLORS.forEach {
            colors.add(it)
        }
        pieDataSet.colors = colors
        pieDataSet.isHighlightEnabled = true
        // 选中的块向外扩大
        pieDataSet.selectionShift = 0f
        // 是否绘制值
        pieDataSet.setDrawValues(false)

        // PieData
        val pieData = PieData(pieDataSet)

        pieChart.data = pieData
        pieChart.centerText = "兴趣区时长\n1小时15分钟"
    }


}
