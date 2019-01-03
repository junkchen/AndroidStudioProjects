package com.junkchen.friendlist


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junkchen.friendlist.adapter.MyRecyclerAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [RecyclerViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RecyclerViewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        init()
        return view
    }

    private fun init() {
        val groupList = mutableListOf<String>()
        for (i in 1..20) {
            if (i < 5) {
                groupList.add("Group #1")
            } else if (i < 8) {
                groupList.add("Group #2")
            } else {
                groupList.add("Group #$i")
            }
        }
        val dataList = mutableListOf<String>()
        for (i in 1..20) {
            dataList.add("child item #$i")
        }
        val myRecyclerAdapter = MyRecyclerAdapter()
        myRecyclerAdapter.dataList = dataList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(SectionDecoration(groupList, context, object : SectionDecoration.DecorationCallback {
            override fun getGroupFirstLine(position: Int): String {
                return groupList[position]
            }

            override fun getGroupId(position: Int): String {
                return groupList[position]
            }
        }))
        recyclerView.adapter = myRecyclerAdapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RecyclerViewFragment.
         */
        @JvmStatic
        fun newInstance() = RecyclerViewFragment()
    }
}
