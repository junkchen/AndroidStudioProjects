package com.junkchen.complexrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Junk on 2017/9/9.
 */

public abstract class TypeAbstractViewHolder<T> extends RecyclerView.ViewHolder {
    public TypeAbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindHolder(T model);
}
