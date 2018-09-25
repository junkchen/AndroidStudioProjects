package com.junkchen.hidebuttonclick

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var txtv_msg: TextView
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtv_msg = findViewById(R.id.txtv_msg)
    }

    fun doClick(view: View) {
        txtv_msg.append("button was click: " + (++count) + "\n")
    }
}
