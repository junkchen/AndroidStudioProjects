package com.junkchen.complexrecyclerview;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Junk on 2017/9/9.
 */

public class DemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    public static final int TYPE_THREE = 3;

    private LayoutInflater mLayoutInflater;
//    private List<DataModel> mList = new ArrayList<>();

    public DemoAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

//    public void addList(List<DataModel> list) {
//        mList.addAll(list);
//    }

    private List<DataModeOne> list1;
    private List<DataModeTwo> list2;
    private List<DataModeThree> list3;
    private List<Integer> types = new ArrayList<>();
    private Map<Integer, Integer> mPositions = new ArrayMap<>();

    public void addList(List<DataModeOne> list1, List<DataModeTwo> list2, List<DataModeThree> list3) {
        addListByType(TYPE_ONE, list1);
        addListByType(TYPE_TWO, list2);
        addListByType(TYPE_THREE, list3);
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
    }

    private void addListByType(int type, List list) {
        mPositions.put(type, types.size());
        for (int i = 0; i < list.size(); i++) {
            types.add(type);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DataModel.TYPE_ONE:
                return new TypeOneViewHolder(
                        mLayoutInflater.inflate(R.layout.item_type_one, parent, false));
            case DataModel.TYPE_TWO:
                return new TypeTwoViewHolder(
                        mLayoutInflater.inflate(R.layout.item_type_two, parent, false));
            case DataModel.TYPE_THREE:
                return new TypeThreeViewHolder(
                        mLayoutInflater.inflate(R.layout.item_type_three, parent, false));
        }
        return null;
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        ((TypeAbstractViewHolder) holder).bindHolder(mList.get(position));
//        //如果没有抽象的 TypeAbstractViewHolder ，每次都需要那个进行强制转换。
////        int viewType = getItemViewType(position);
////        switch (viewType) {
////            case DataModel.TYPE_ONE:
////                ((TypeOneViewHolder) holder).bindHolder(mList.get(position));
////                break;
////            case DataModel.TYPE_TWO:
////                ((TypeTwoViewHolder) holder).bindHolder(mList.get(position));
////                break;
////            case DataModel.TYPE_THREE:
////                ((TypeThreeViewHolder) holder).bindHolder(mList.get(position));
////                break;
////        }
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        ((TypeAbstractViewHolder) holder).bindHolder(mList.get(position));

        int viewType = getItemViewType(position);
        int realPosition = position - mPositions.get(viewType);

        //如果没有抽象的 TypeAbstractViewHolder ，每次都需要那个进行强制转换。
        switch (viewType) {
            case DataModel.TYPE_ONE:
                ((TypeOneViewHolder) holder).bindHolder(list1.get(realPosition));
                break;
            case DataModel.TYPE_TWO:
                ((TypeTwoViewHolder) holder).bindHolder(list2.get(realPosition));
                break;
            case DataModel.TYPE_THREE:
                ((TypeThreeViewHolder) holder).bindHolder(list3.get(realPosition));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return mList.get(position).type;
        return types.get(position);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }
}
