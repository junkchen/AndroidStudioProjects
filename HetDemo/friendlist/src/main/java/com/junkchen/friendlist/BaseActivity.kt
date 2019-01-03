package com.junkchen.friendlist

import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}