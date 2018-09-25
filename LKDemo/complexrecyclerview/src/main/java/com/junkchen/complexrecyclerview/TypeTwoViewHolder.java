package com.junkchen.complexrecyclerview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Junk on 2017/9/9.
 */

public class TypeTwoViewHolder extends TypeAbstractViewHolder<DataModeTwo> {
    public ImageView avatar;
    public TextView name;
    public TextView content;

    public TypeTwoViewHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(Color.GREEN);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        name = (TextView) itemView.findViewById(R.id.name);
        content = (TextView) itemView.findViewById(R.id.content);
    }

    @Override
    public void bindHolder(DataModeTwo model) {
        avatar.setBackgroundResource(model.avatarColor);
        name.setText(model.name);
        content.setText(model.content);
    }
}
