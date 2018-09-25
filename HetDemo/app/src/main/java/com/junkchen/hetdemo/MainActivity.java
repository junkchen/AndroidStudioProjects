package com.junkchen.hetdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ListView.CHOICE_MODE_MULTIPLE
//        RecyclerView
    }

    public void doClick(View view) {
        if (view.getId() == R.id.tv_hello) {
            startActivity(new Intent(this, SurveyActivity.class));
        } else {
            startActivity(new Intent(this, AnswerActivity.class));
        }
    }
}
