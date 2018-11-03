package com.junkchen.customview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junk Chen on 2017/9/3.
 */
public class FlowLayout extends ViewGroup {
    public static final String TAG = FlowLayout.class.getCanonicalName();

    /**
     * 保存每行 view 的列表
     */
    private List<List<View>> mViewRowList = new ArrayList<>();

    /**
     * 保存行高的列表
     */
    private List<Integer> mRowHeightList = new ArrayList<>();

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取父容器为FlowLayout设置的测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;
        int currentRowWidth = 0;
        int currentRowHeight = 0;

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            Log.i(TAG, "onMeasure: This mode is EXACTLY.");
            measureWidth = widthSpecSize;
            measureHeight = heightSpecSize;
        } else {
            Log.i(TAG, "onMeasure: This mode is not EXACTLY.");
            int childWidth = 0;
            int childHeight = 0;

            int childCount = getChildCount();
            List<View> viewList = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);

                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                childWidth = childView.getMeasuredWidth() +
                        layoutParams.leftMargin + layoutParams.rightMargin;
                childHeight = childView.getMeasuredHeight() +
                        layoutParams.topMargin + layoutParams.bottomMargin;

                if (currentRowWidth + childWidth > widthSpecSize) {
                    // 记录当前行信息
                    // 1、记录当前行的最大宽度，高度累加
                    measureWidth = Math.max(measureWidth, currentRowWidth);
                    measureHeight += currentRowHeight;

                    // 2、将当前行的 viewList 添加到总的 mViewRowList 中，将行高添加到总的行高 list
                    mViewRowList.add(viewList);
                    mRowHeightList.add(currentRowHeight);

                    // 记录新一行的信息
                    // 1、重新赋值新一行的宽、高
                    currentRowWidth = childWidth;
                    currentRowHeight = childHeight;

                    // 2、新建一行的viewList，添加新一行的 view
                    viewList = new ArrayList<>();
                    viewList.add(childView);

                } else {
                    // 记录某行内的消息
                    // 1、行内宽度的叠加、高度的比较
                    currentRowWidth += childWidth;
                    currentRowHeight = Math.max(currentRowHeight, childHeight);

                    // 2、添加至当前行的 viewList 中
                    viewList.add(childView);
                }

                // 如果正好是最后一行需要换行
                if (i == childCount - 1) {
                    // 1、记录当前行的最大宽度，高度累加
                    measureWidth = Math.max(measureWidth, currentRowWidth);
                    measureHeight += currentRowHeight;

                    // 2、将当前行的 viewList 添加到总的 mViewRowList 中，将行高添加到总的行高 list
                    mViewRowList.add(viewList);
                    mRowHeightList.add(currentRowHeight);
                }
            }
        }

        Log.i(TAG, "onMeasure: measureWidth = " + measureWidth + ", measureHeight = " + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout: l = " + l + ", t = " + t + ", r = " + r + ", b = " + b);
        int left, top, right, bottom;
        int currentLeft = 0, currentTop = 0;

        int rowCount = mViewRowList.size();
        for (int i = 0; i < rowCount; i++) {
            List<View> viewList = mViewRowList.get(i);
            int rowViewSize = viewList.size();
            for (int j = 0; j < rowViewSize; j++) {
                View childView = viewList.get(j);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();

                left = currentLeft + layoutParams.leftMargin;
                top = currentTop + layoutParams.topMargin;
                right = left + childView.getMeasuredWidth();
                bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);
                currentLeft += childView.getMeasuredWidth() +
                        layoutParams.leftMargin + layoutParams.rightMargin;
            }
            // 一行摆放完要置0
            currentLeft = 0;
            currentTop += mRowHeightList.get(i);
        }

        // 坑，必须清除,测量可能执行多次
        mViewRowList.clear();
        mRowHeightList.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int index);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            final int finalI = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(childView, finalI);
                }
            });
        }
    }
}
