package com.junkchen.autocloseapp;

import android.app.Application;

/**
 * Created by Junk on 2017/8/19.
 */

public class MyApplication extends Application {
    private static ApplicationSubject applicationSubject;

    public static ApplicationSubject getApplicationSubject() {
        if (applicationSubject == null) {
            applicationSubject = new ApplicationSubject();
        }
        return applicationSubject;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ActivityStack.getInstance();
    }
}
