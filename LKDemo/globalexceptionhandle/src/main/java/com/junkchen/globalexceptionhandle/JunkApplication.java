package com.junkchen.globalexceptionhandle;

import android.app.Application;

/**
 * Created by Junk on 2017/11/23.
 */

public class JunkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }
}
