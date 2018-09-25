package com.junkchen.complexrecyclerview;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Junk on 2017/9/9.
 */

public class TypeOneViewHolder extends TypeAbstractViewHolder<DataModeOne> {
    public ImageView avatar;
    public TextView name;

    public TypeOneViewHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(Color.CYAN);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        name = (TextView) itemView.findViewById(R.id.name);
    }

    @Override
    public void bindHolder(DataModeOne model) {
        avatar.setBackgroundResource(model.avatarColor);
        name.setText(model.name);
    }
}
