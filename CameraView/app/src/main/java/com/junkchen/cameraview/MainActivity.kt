package com.junkchen.cameraview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu

/**
 * Created by Junk Chen on 2019/7/2.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (null == savedInstanceState) {
            supportFragmentManager?.beginTransaction()
//                ?.replace(R.id.container, Camera2Fragment.newInstance())
//                ?.replace(R.id.container, Camera2sFragment.newInstance())
                ?.replace(R.id.container, JcameraFragment.newInstance())
//                ?.replace(R.id.container, TexturePlayVideoFragment.newInstance())
                ?.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
