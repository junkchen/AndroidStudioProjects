package com.junkchen.friendlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import com.junkchen.friendlist.adapter.FriendExpandableAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val groupList = arrayListOf<String>()
        for (i in 1..6) {
            groupList.add("Group #$i")
        }
        val childList = mutableListOf<MutableList<String>>()
        for (i in 1..6) {
            val childs = mutableListOf<String>()
            for (j in 1..10) {
                childs.add("child item #$j")
            }
            childList.add(childs)
        }
        val friendExpandableAdapter = FriendExpandableAdapter(this, groupList, childList)
        elv_friend.setAdapter(friendExpandableAdapter)
//        elv_friend.descendantFocusability = ExpandableListView.FOCUS_AFTER_DESCENDANTS
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        elv_friend.setIndicatorBounds(displayMetrics.widthPixels - 96, displayMetrics.widthPixels - 16)
        elv_friend.expandGroup(0, true)

        elv_friend.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            Log.i(TAG, "setOnChildClickListener: groupPosition=$groupPosition, childPosition=$childPosition")
            false
        }
        elv_friend.setOnGroupClickListener { parent, v, groupPosition, id ->
            Log.i(TAG, "setOnChildClickListener: groupPosition=$groupPosition")
            false // 请务必返回false，否则分组不会展开
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
