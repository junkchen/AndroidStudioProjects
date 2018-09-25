package com.junkchen.viewstubdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewStub
import android.widget.TextView

class MainActivity : AppCompatActivity() {

//    val TAG = MainActivity::class.simpleName

    lateinit var mViewStub: ViewStub
    lateinit var txtv_content: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewStub = findViewById(R.id.stub)
    }

    fun doClick(view: View) {
        when (view.id) {
            R.id.btn_inflate -> {
                val view = mViewStub.inflate()
                txtv_content = findViewById(R.id.txtv_content)
            }
            R.id.btn_set -> {
//                txtv_content.setText("I'm new data.")
                txtv_content.text = "I'm new data."
            }
        }
    }
}
