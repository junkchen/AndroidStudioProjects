package com.junkchen.androidlifecycles.step3

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.junkchen.androidlifecycles.R
import kotlinx.android.synthetic.main.activity_chrono3.*

class Chrono3Activity : AppCompatActivity() {
    companion object {
        private val TAG = Chrono3Activity::class.java.canonicalName
    }

    private lateinit var liveDataTimerViewModel: LiveDataTimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chrono3)
        liveDataTimerViewModel = ViewModelProviders.of(this)
                .get(LiveDataTimerViewModel::class.java)
        subscribe()
    }

    private fun subscribe() {
        val elapsedTimeObserver = Observer<Long> { time ->
            val newText = this@Chrono3Activity.resources.getString(R.string.seconds, time)
            tv_timer.text = newText
            Log.i(TAG, "Updating timer.")
        }

        // observe the ViewModel's elapsed time
        liveDataTimerViewModel.elapsedTime.observe(this, elapsedTimeObserver)
    }
}
