package com.junkchen.threadpooldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.junkchen.threadpooldemo.thread.ThreadPoolUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createThreadPool();
//        ThreadPoolUtils.getInstance().execute(
//                () -> Log.i(TAG, "I'm ThreadPoolUtils. 1-Thread name: " +
//                        Thread.currentThread().getName() + ", id:" +Thread.currentThread().getId())
//        );
//        ThreadPoolUtils.getInstance().execute(
//                () -> Log.i(TAG, "I'm ThreadPoolUtils. 2-Thread name: " +
//                        Thread.currentThread().getName() + ", id:" +Thread.currentThread().getId())
//        );
    }

    private void createThreadPool() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize * 2;
        long keepAliveTime = 1;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

        ExecutorService mExecutorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue
        );
//        mExecutorService.

        ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue
        );
        mExecutor.execute(
                () -> {
                    Log.i(TAG, "createThreadPool: corePoolSize: " + corePoolSize);
                    Log.i(TAG, "createThreadPool: maxPoolSize: " + maxPoolSize);
                }
        );
        mExecutor.execute(
                () -> {
                    Log.i(TAG, "createThreadPool: 2-corePoolSize: " + corePoolSize);
                    Log.i(TAG, "createThreadPool: 2-maxPoolSize: " + maxPoolSize);
                }
        );
        Log.i(TAG, "createThreadPool: ActiveCount: " + mExecutor.getActiveCount() +
                ", CompletedTaskCount: " + mExecutor.getCompletedTaskCount() +
                ", TaskCount: " + mExecutor.getTaskCount());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "02-createThreadPool: ActiveCount: " + mExecutor.getActiveCount() +
                ", CompletedTaskCount: " + mExecutor.getCompletedTaskCount() +
                ", TaskCount: " + mExecutor.getTaskCount());
        try {
//            mExecutor.shutdown();
            mExecutor.purge();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "03-createThreadPool: ActiveCount: " + mExecutor.getActiveCount() +
                ", CompletedTaskCount: " + mExecutor.getCompletedTaskCount() +
                ", TaskCount: " + mExecutor.getTaskCount());
    }
}
