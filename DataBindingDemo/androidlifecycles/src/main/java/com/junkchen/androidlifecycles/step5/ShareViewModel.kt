package com.junkchen.androidlifecycles.step5

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    var seekBarValue = MutableLiveData<Int>()
}