package com.junkchen.androidlifecycles.step2

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.junkchen.androidlifecycles.R
import kotlinx.android.synthetic.main.activity_chrono.*

class Chrono2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrono)

        val chronometerViewModel = ViewModelProviders.of(this)
                .get(ChronometerViewModel::class.java)

        if (chronometerViewModel.startTime == null) {
            val startTime = SystemClock.elapsedRealtime()
            chronometerViewModel.startTime = startTime
            chronometer.base = startTime
        } else {
            chronometer.base = chronometerViewModel.startTime!!
        }

        chronometer.start()
    }
}
