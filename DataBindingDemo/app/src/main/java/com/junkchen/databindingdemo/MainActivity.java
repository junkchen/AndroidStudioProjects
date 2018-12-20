package com.junkchen.databindingdemo;

import androidx.lifecycle.LiveData;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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
