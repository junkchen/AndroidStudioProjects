package com.junkchen.complexrecyclerview.select;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junkchen.complexrecyclerview.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Junk on 2017/9/19.
 */

public class MultiSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private boolean isMultiSelectedMode = false;//Current mode if multi selected.
    private Set<Integer> mSelectedPosition;//Selected position set.

    public MultiSelectAdapter() {
        this.mData = new ArrayList<>();
        mSelectedPosition = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SelectViewHolder)holder).bindHolder(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<String> data) {
        this.mData = data;
    }

    class SelectViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView txtv_content;

        public SelectViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            txtv_content = (TextView) itemView.findViewById(R.id.txtv_content);
        }

        public void bindHolder(String content, final int position) {
            txtv_content.setText(content);
            if (mSelectedPosition.contains(position)) {
                itemView.setBackgroundColor(Color.BLUE);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMultiSelectedMode) {//Is multi selected mode
                        if (mSelectedPosition.contains(position)) {
                            mSelectedPosition.remove(position);
                        } else {
                            mSelectedPosition.add(position);
                        }
                        notifyItemChanged(position);
                    } else {//Is not multi selected mode

                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isMultiSelectedMode) {
                        isMultiSelectedMode = false;
                        mSelectedPosition.clear();
                        notifyDataSetChanged();
                        return true;
                    } else {
                        isMultiSelectedMode = true;
                        mSelectedPosition.add(position);
                        notifyItemChanged(position);
                        return true;
                    }
//                    return false;
                }
            });
        }
    }
}
