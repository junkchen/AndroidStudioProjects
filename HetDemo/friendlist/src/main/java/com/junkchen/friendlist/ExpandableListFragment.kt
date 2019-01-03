package com.junkchen.friendlist


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.junkchen.friendlist.adapter.FriendExpandableAdapter

/**
 * A simple [Fragment] subclass.
 *
 */
class ExpandableListFragment : Fragment() {

    private lateinit var elv_friend: ExpandableListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expandable_list, container, false)
        elv_friend = view.findViewById(R.id.elv_friend)
        init()
        return view
    }

    private fun init() {
        val groupList = arrayListOf<String>()
        for (i in 1..6) {
            groupList.add("Group #$i")
        }
        val childList = mutableListOf<MutableList<String>>()
        for (i in 1..6) {
            val childs = mutableListOf<String>()
            for (j in 1..6) {
                childs.add("child item #$j")
            }
            childList.add(childs)
        }
        val friendExpandableAdapter = FriendExpandableAdapter(this.context!!, groupList, childList)
        elv_friend.setAdapter(friendExpandableAdapter)
//        elv_friend.descendantFocusability = ExpandableListView.FOCUS_AFTER_DESCENDANTS
        val displayMetrics = DisplayMetrics()
        this.activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
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
        private val TAG = ExpandableListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() = ExpandableListFragment()
    }
}
