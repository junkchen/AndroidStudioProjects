package com.junkchen.zxingdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false).initiateScan()

        btn_scan_qrcode.setOnClickListener {
//            integrator.initiateScan()
//            startActivity(Intent(this, CustomCaptureActivity::class.java))
            integrator.captureActivity = CustomCaptureActivity::class.java
            integrator.initiateScan()
        }
    }

    // Get the results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            tv_scan_result.text = result.contents ?: "Cancelled"
//            val contents = result.contents ?: "Cancelled"
//            Toast.makeText(this, contents, Toast.LENGTH_SHORT).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
