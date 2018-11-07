package com.junkchen.androidlifecycles.step5

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.junkchen.androidlifecycles.R

class ShareViewModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_view_model)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment1, ShareFragment())
                .add(R.id.fragment2, ShareFragment())
                .commitNow()
    }
}
