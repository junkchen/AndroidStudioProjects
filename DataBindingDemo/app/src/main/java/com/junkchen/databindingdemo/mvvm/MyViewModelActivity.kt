package com.junkchen.databindingdemo.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junkchen.databindingdemo.R
import com.junkchen.databindingdemo.mvvm.ui.myviewmodel.MyViewModelFragment

class MyViewModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_view_model_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MyViewModelFragment.newInstance())
                    .commitNow()
        }
    }

}
