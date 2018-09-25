package com.junkchen.databindingdemo;

import android.view.View;
import android.widget.Toast;

public class Presenter {
    public void doClick(View view, User user) {
        user.setFirstName("Kotlin");
        Toast.makeText(view.getContext(), user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
    }
}
