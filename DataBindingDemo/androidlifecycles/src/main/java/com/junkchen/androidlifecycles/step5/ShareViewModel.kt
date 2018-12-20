package com.junkchen.androidlifecycles.step5

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    var seekBarValue = MutableLiveData<Int>()
}