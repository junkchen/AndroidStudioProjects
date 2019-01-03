package com.junkchen.friendlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun byExpandableListView(view: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, ExpandableListFragment.newInstance())
                .addToBackStack(ExpandableListFragment::class.java.simpleName)
                .commit()
    }

    fun byRecyclerView(view: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, RecyclerViewFragment.newInstance())
                .addToBackStack(RecyclerViewFragment::class.java.simpleName)
                .commit()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
