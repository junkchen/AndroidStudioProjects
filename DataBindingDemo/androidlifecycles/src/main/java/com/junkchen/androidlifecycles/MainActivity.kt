package com.junkchen.androidlifecycles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.junkchen.androidlifecycles.step1.Chrono1Activity
import com.junkchen.androidlifecycles.step2.Chrono2Activity
import com.junkchen.androidlifecycles.step3.Chrono3Activity
import com.junkchen.androidlifecycles.step4.LocationActivity
import com.junkchen.androidlifecycles.step5.ShareViewModelActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_step1.setOnClickListener {
            startActivity(Intent(this, Chrono1Activity::class.java))
        }

        btn_step2.setOnClickListener {
            startActivity(Intent(this, Chrono2Activity::class.java))
        }

        btn_step3.setOnClickListener {
            startActivity(Intent(this, Chrono3Activity::class.java))
        }

        btn_step4.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }

        btn_step5.setOnClickListener {
            startActivity(Intent(this, ShareViewModelActivity::class.java))
        }
    }
}
