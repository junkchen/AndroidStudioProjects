package com.junkchen.autocloseapp;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

/**
 * This class is use for finish Activity.
 * Created by Junk on 2017/8/20.
 */

public class ActivityStack {
    public static final String TAG = ActivityStack.class.getSimpleName();

    private Stack<Activity> stack = new Stack<>();

    private static final ActivityStack ourInstance = new ActivityStack();

    static ActivityStack getInstance() {
        return ourInstance;
    }

    private ActivityStack() {
    }

    public Activity pushActivity(Activity activity) {
        if (activity != null) {
            return stack.push(activity);
        }
        return null;
    }

    public boolean addActivity(Activity activity) {
        if (activity != null) {
            return stack.add(activity);
        }
        return false;
    }

    public Activity peekActivity() {
        return stack.peek();
    }

    private Activity popActivity() {
        return stack.pop();
    }

    public boolean removeActivity(Activity activity) {
        return stack.removeElement(activity);
    }

    public int size() {
        return stack.size();
    }

    public void finishActivity(Activity activity) {
        Log.i(TAG, "finishActivity: name: " + activity.getClass().getSimpleName());
        Log.i(TAG, "finishAllActivity: class: " + activity.getClass());
        activity.finish();
    }

    public void finishActivity(Class<? extends Activity> clazz) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(clazz)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        while (!stack.empty() && stack.peek() != null) {
            finishActivity(stack.peek());
        }
    }
}
