package com.junkchen.databindingdemo;

import android.arch.lifecycle.LiveData;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.junkchen.databindingdemo.databinding.ActivityDataBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data);
        ActivityDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_data);

        User user = new User("Junk", "Chen");
        binding.setUser(user);
        binding.setPresenter(new Presenter());

    }
}
