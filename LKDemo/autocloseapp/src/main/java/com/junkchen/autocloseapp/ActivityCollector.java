package com.junkchen.autocloseapp;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Junk on 2017/8/20.
 */

public class ActivityCollector {
    public static final String TAG = ActivityCollector.class.getSimpleName();
    private List<Activity> activities = new ArrayList<>();

    private static final ActivityCollector instance = new ActivityCollector();

    static ActivityCollector getInstance() {
        return instance;
    }

    private ActivityCollector() {

    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public boolean removeActivity(Activity activity) {
        if (activities.contains(activity)) {
            return activities.remove(activity);
        }
        return false;
    }

    public int size() {
        return activities.size();
    }

    public void finishActivity(Activity activity) {
        activity.finish();
        removeActivity(activity);
    }

    public void finishActivity(Class<? extends Activity> clazz) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(clazz)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
//        for (Activity activity : activities) {
//            Log.i(TAG, "finishAllActivity: name: " + activity.getClass().getSimpleName());
//            activity.finish();
//        }
        while (activities.size() > 0) {
            int i = activities.size() - 1;
            Activity activity = activities.get(i);
            Log.i(TAG, "finishAllActivity: name: " + activity.getClass().getSimpleName());
            Log.i(TAG, "finishAllActivity: class: " + activity.getClass());
//            activity.finish();
//            activities.remove(i);
            finishActivity(activity);
        }
//        activities.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
