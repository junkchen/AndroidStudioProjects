package com.junkchen.complexrecyclerview;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Junk on 2017/9/9.
 */

public class TypeThreeViewHolder extends TypeAbstractViewHolder<DataModeThree> {
    public ImageView avatar;
    public TextView name;
    public TextView content;
    public ImageView contentColor;

    public TypeThreeViewHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(Color.GRAY);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        contentColor = (ImageView) itemView.findViewById(R.id.contentColor);
        name = (TextView) itemView.findViewById(R.id.name);
        content = (TextView) itemView.findViewById(R.id.content);
    }

    @Override
    public void bindHolder(DataModeThree model) {
        avatar.setBackgroundResource(model.avatarColor);
        name.setText(model.name);
        content.setText(model.content);
        contentColor.setBackgroundResource(model.contentColor);
    }
}
