package com.junkchen.friendlist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.junkchen.friendlist.R

class MyRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: MutableList<String> = arrayListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_child_view, p0, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        p0 as MyViewHolder
        p0.bindData(dataList[p1])
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tv_child = view.findViewById<TextView>(R.id.tv_child)

        fun bindData(content: String) {
            tv_child.text = content
        }
    }
}