package com.junkchen.timepickerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.WheelAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private PickerView mPickerView;
    private WheelView mWheelView;
    private com.bigkoo.pickerview.lib.WheelView mWheelView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPickerView = (PickerView) findViewById(R.id.mPickerView);
        mWheelView = (WheelView)findViewById(R.id.mWheelView);
        mWheelView2 = (com.bigkoo.pickerview.lib.WheelView) findViewById(R.id.mWheelView2);

        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("" + i);
        }
        mPickerView.setData(data);
        mWheelView.setData(data);
        WheelAdapter<String> adapter = new ArrayWheelAdapter<>(data);
        mWheelView2.setAdapter(adapter);
    }

    public void showTimePicker(View view) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
//                tvTime.setText(getTime(date));
            }
        })
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。

        pvTime.show();


    }
}
