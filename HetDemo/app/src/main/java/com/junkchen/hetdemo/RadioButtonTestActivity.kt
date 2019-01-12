package com.junkchen.hetdemo

import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_radio_button_test.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.util.*

class RadioButtonTestActivity : AppCompatActivity() {

    companion object {
        private val TAG = "RadioButtonTestActivity"
    }

    private lateinit var badge: Badge

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                Log.i(TAG, "onNavigationItemSelected: ${R.string.title_home}")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                Log.i(TAG, "onNavigationItemSelected: ${R.string.title_dashboard}")
                val random = Random()
                badge.badgeNumber = random.nextInt(178)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                Log.i(TAG, "onNavigationItemSelected: ${R.string.title_notifications}")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio_button_test)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.itemTextColor
        navigation.itemBackgroundResource

        rg_navigator.setOnCheckedChangeListener { group, checkedId ->
            Log.i(TAG, "onCheckedChanged: checkedId=$checkedId")
            Snackbar.make(container, "checkedId=$checkedId", Snackbar.LENGTH_SHORT).show()
        }

        val navigation_home = navigation.findViewById<BottomNavigationItemView>(R.id.navigation_dashboard)
        badge = QBadgeView(this).apply {
            this.bindTarget(navigation_home)
            this.isShowShadow = true
            this.badgeGravity = Gravity.END or Gravity.TOP
            this.setGravityOffset(16f, 0f, true)
            this.badgeNumber = 3
//            this.setOnDragStateChangedListener { dragState, badge, targetView ->
//                badge.badgeNumber = 6
//            }
        }
    }
}
