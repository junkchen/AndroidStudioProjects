package com.junkchen.androidlifecycles.step3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.SystemClock
import java.util.*
import kotlin.concurrent.timerTask

class LiveDataTimerViewModel : ViewModel() {

    companion object {
        private const val ONE_SECOND = 1000L
    }

    val elapsedTime = MutableLiveData<Long>()

    private var mInitialTime = 0L

    init {
        mInitialTime = SystemClock.elapsedRealtime()
        val timer = Timer()

        // Update the elapsed time every second.
        timer.scheduleAtFixedRate(timerTask {
            val newValue = (SystemClock.elapsedRealtime() - mInitialTime) / 1000
            // setValue() cannot be called from a background thread so post to main thread.
            // post the new value with LiveData.postValue()
            elapsedTime.postValue(newValue)
        }, ONE_SECOND, ONE_SECOND)
    }
}