package com.junkchen.threadpooldemo.thread;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Junk Chen on 2018/04/24.
 */
public class ThreadPoolUtils {

    private ThreadPoolExecutor mExecutor;
    private static final ThreadPoolUtils ourInstance = new ThreadPoolUtils();

    public static ThreadPoolUtils getInstance() {
        return ourInstance;
    }

    private ThreadPoolUtils() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize * 2;
        long keepAliveTime = 1;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

        mExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue
        );
    }

    /**
     * Same as {@link ThreadPoolExecutor#execute(Runnable)}.
     */
    public void execute(Runnable task) {
        mExecutor.execute(task);
    }

    /**
     * Same as {@link ThreadPoolExecutor#remove(Runnable)}.
     */
    public boolean remove(Runnable task) {
        return mExecutor.remove(task);
    }

    /**
     * Same as {@link ThreadPoolExecutor#purge()}.
     */
    public void purge() {
        mExecutor.purge();
    }

    /**
     * Same as {@link ThreadPoolExecutor#shutdown()}.
     */
    public void shutdown() {
        mExecutor.shutdown();
    }

    /**
     * Same as {@link ThreadPoolExecutor#shutdownNow()}.
     */
    public List<Runnable> shutdownNow() {
        return mExecutor.shutdownNow();
    }
}
