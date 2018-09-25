package com.junkchen.cardviewdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ThirdActivity extends AppCompatActivity {
    private Button imgv_mask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        imgv_mask = (Button) findViewById(R.id.imgv_mask);
        imgv_mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgv_mask.setVisibility(View.GONE);
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                imgv_mask.setVisibility(View.GONE);
//            }
//        }, 1800);
    }
}
