package com.junkchen.androidlifecycles.step1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.junkchen.androidlifecycles.R
import kotlinx.android.synthetic.main.activity_chrono.*

class Chrono1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrono)

        chronometer.start()
    }
}
