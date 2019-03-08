package com.junkchen.inputbox

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tuo.customview.VerificationCodeView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        icv.setInputCompleteListener(object: VerificationCodeView.InputCompleteListener {
            override fun inputComplete() {
                Log.i(TAG, "inputComplete: 输入内容: ${icv.inputContent}")
            }

            override fun deleteContent() {
                Log.i(TAG, "deleteContent: input content: ${icv.inputContent}")
            }
        })
    }
}
