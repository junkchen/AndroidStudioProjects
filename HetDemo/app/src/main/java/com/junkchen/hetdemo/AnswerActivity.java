package com.junkchen.hetdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.MutableChar;
import android.util.SparseArray;
import android.view.View;

import com.junkchen.hetdemo.adapter.QuestionFragmentAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnswerActivity extends AppCompatActivity implements AnswerFragment.OnFragmentInteractionListener {
    private ViewPager vp_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        int questionType = new Random().nextInt(2);
//        if (questionType == 1) {
//            transaction.add(R.id.fl_answer, AnswerFragment.newInstance("宝宝属于那种性格类型呢？", 1));
//        } else {
//            transaction.add(R.id.fl_answer, AnswerFragment.newInstance("宝宝常表现有以下哪些行为？", 2));
//        }
//        transaction.commit();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(AnswerFragment.newInstance("宝宝属于那种性格类型呢？", 1, 1));
        fragmentList.add(AnswerFragment.newInstance("宝宝常表现有以下哪些行为？", 2, 2));

        vp_answer = findViewById(R.id.vp_answer);
        vp_answer.setAdapter(new QuestionFragmentAdapter(getSupportFragmentManager(), fragmentList));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onButtonClicked(int i) {
        if (i == 1) {
            vp_answer.setCurrentItem(1);
        } else {
            vp_answer.setCurrentItem(0);
        }
    }

}
