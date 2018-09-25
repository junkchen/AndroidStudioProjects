package com.junkchen.complexrecyclerview.select;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.junkchen.complexrecyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectItemActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MultiSelectAdapter mMultiSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_select_item);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMultiSelectAdapter = new MultiSelectAdapter();
        mRecyclerView.setAdapter(mMultiSelectAdapter);

        makeData();
    }

    private void makeData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            data.add("Data item #" + i);
        }

        mMultiSelectAdapter.setData(data);
        mMultiSelectAdapter.notifyDataSetChanged();
    }
}
