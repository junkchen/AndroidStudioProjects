package com.junkchen.databindingdemo;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.junkchen.databindingdemo.mvvm.MyViewModelActivity;

public class Presenter {
    public void doClick(View view, User user) {
        user.setFirstName("Kotlin");
        Toast.makeText(view.getContext(), user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
        view.getContext().startActivity(new Intent(view.getContext(), MyViewModelActivity.class));
    }
}
