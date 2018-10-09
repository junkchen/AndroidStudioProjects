package com.junkchen.chartdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.junkchen.chartdemo.widget.WaterPieChart

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(WaterPieChart(this))
    }
}
