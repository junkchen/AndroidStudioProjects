package com.example.junk.myapplication

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

class AutoRadioActivity : AppCompatActivity() {
    val TAG = "AutoRadioActivity"
    var mRadioGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_radio)

        mRadioGroup = this.findViewById(R.id.mRadioGroup) as RadioGroup?
        var layoutParams = mRadioGroup?.layoutParams
        Log.i(TAG, "layoutParams.height = " + layoutParams!!.height + ", width = " + layoutParams!!.width)
        Log.i(TAG, "layoutParams.height = " + layoutParams!!.height + ", width = " + mRadioGroup!!.measuredWidth)

        val radio1 = RadioButton(this)
        radio1.width = mRadioGroup!!.measuredWidth / 2
        radio1.height = layoutParams!!.height
//        radio1.height = ViewGroup.LayoutParams.MATCH_PARENT
        radio1.text = "脂肪"
        radio1.textSize = 24F
        radio1.gravity = Gravity.CENTER
        radio1.buttonDrawable = null
        radio1.setBackgroundColor(Color.RED)

        val radio2 = RadioButton(this)
        radio2.width = layoutParams.width / 2
        radio2.height = layoutParams!!.height
//        radio2.height = ViewGroup.LayoutParams.MATCH_PARENT
        radio2.text = "水分"
        radio2.textSize = 24F
        radio2.gravity = Gravity.CENTER
        radio2.buttonDrawable = null
        radio2.setBackgroundColor(Color.CYAN)

//        layoutParams?.height =  ViewGroup.LayoutParams.MATCH_PARENT
        mRadioGroup?.addView(radio1)
        mRadioGroup?.addView(radio2)
//        mRadioGroup?.addView(radio1, layoutParams)

    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: mRadioGroup.height = " + mRadioGroup!!.measuredHeight + ", width = " + mRadioGroup!!.measuredWidth)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: mRadioGroup.height = " + mRadioGroup!!.measuredHeight + ", width = " + mRadioGroup!!.measuredWidth)
    }
}
