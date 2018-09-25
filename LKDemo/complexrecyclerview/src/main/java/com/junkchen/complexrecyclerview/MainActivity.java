package com.junkchen.complexrecyclerview;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DemoAdapter mAdapter;

    int colors[] = new int[]{
            android.R.color.holo_red_dark,
            android.R.color.holo_blue_dark,
            android.R.color.holo_orange_dark
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false));

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mAdapter.getItemViewType(position);
                if (viewType == DataModel.TYPE_THREE) {
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new DemoAdapter(this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams =
                        (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                int spanIndex = layoutParams.getSpanIndex();
                outRect.top = 20;
//                outRect.bottom = 10;
                if (spanSize != gridLayoutManager.getSpanCount()) {
                    if (spanIndex == 0) {
//                        outRect.left = 10;
                        outRect.right = 10;
                    } else {
                        outRect.left = 10;
//                        outRect.right = 10;
                    }
                }
            }
        });

        initData();
    }

    private void initData() {
        List<DataModel> list = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
//            int type = (int) (Math.random() * 3 + 1);
            int type;
            if (i < 5 || (i > 15 && i < 20)) {
                type = 1;
            } else if (i < 11 || i > 24) {
                type = 2;
            } else {
                type = 3;
            }
            DataModel data = new DataModel();
            data.type = type;
            data.avatarColor = colors[type - 1];
            data.name = "Name: " + type;
            data.content = "Content: " + i;
            data.contentColor = colors[(type + 1) % 3];
            list.add(data);
        }

        List<DataModeOne> list1 = new ArrayList<>();
        List<DataModeTwo> list2 = new ArrayList<>();
        List<DataModeThree> list3 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            DataModeOne data = new DataModeOne();
            data.avatarColor = colors[0];
            data.name = "Name: " + 1;
            list1.add(data);
        }

        for (int i = 0; i < 10; i++) {
            DataModeTwo data = new DataModeTwo();
            data.avatarColor = colors[1];
            data.name = "Name: " + 2;
            data.content = "Content " + i;
            list2.add(data);
        }

        for (int i = 0; i < 10; i++) {
            DataModeThree data = new DataModeThree();
            data.avatarColor = colors[2];
            data.name = "Name: " + 3;
            data.content = "Content " + i;
            data.contentColor = colors[2];
            list3.add(data);
        }

//        mAdapter.addList(list);
        mAdapter.addList(list1, list2, list3);
        mAdapter.notifyDataSetChanged();
    }
}
