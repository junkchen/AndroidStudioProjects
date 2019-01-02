package com.junkchen.friendlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.junkchen.friendlist.R


class FriendExpandableAdapter(val context: Context,
                              var groupList: MutableList<String>,
                              var childList: MutableList<MutableList<String>>) : BaseExpandableListAdapter() {

    private var layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getGroup(groupPosition: Int): String {
        return groupList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        // 很重要：实现ChildView点击事件，必须返回true
        return true
    }

    override fun hasStableIds(): Boolean {
        // 当子条目ID相同时是否复用
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupHolder: GroupViewHolder
        var convertView = convertView
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_group_view, null)
            groupHolder = GroupViewHolder()
            convertView.tag = groupHolder
        } else {
            groupHolder = convertView.tag as GroupViewHolder
        }
        groupHolder.tv_group = convertView?.findViewById(R.id.tv_group)
        groupHolder.tv_group?.text = getGroup(groupPosition)
        return convertView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return childList[groupPosition].size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return childList[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup?): View {
        val childHolder: ChildViewHolder
        var convertView = convertView
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_child_view, null)
            childHolder = ChildViewHolder()
            convertView.tag = childHolder
        } else {
            childHolder = convertView.tag as ChildViewHolder
        }
        childHolder.tv_child = convertView?.findViewById(R.id.tv_child)
        childHolder.tv_child?.text = getChild(groupPosition, childPosition)
        return convertView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return groupList.size
    }

    private inner class GroupViewHolder {
        internal var tv_group: TextView? = null
    }

    inner class ChildViewHolder {
        internal var tv_child: TextView? = null
    }
}