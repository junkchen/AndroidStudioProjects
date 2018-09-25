package com.junkchen.threadpooldemo

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by Junk Chen on 2018/04/24.
 */
class ThreadPoolUtils private constructor() {

    private val mExecutor: ThreadPoolExecutor

    init {
        val corePoolSize = Runtime.getRuntime().availableProcessors()
        val maxPoolSize = corePoolSize * 2
        val keepAliveTime: Long = 1
        val workQueue = LinkedBlockingQueue<Runnable>()

        mExecutor = ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue
        )
    }

    /**
     * Same as [ThreadPoolExecutor.execute].
     */
    fun execute(task: Runnable) {
        mExecutor.execute(task)
    }

    /**
     * Same as [ThreadPoolExecutor.remove].
     */
    fun remove(task: Runnable): Boolean {
        return mExecutor.remove(task)
    }

    /**
     * Same as [ThreadPoolExecutor.purge].
     */
    fun purge() {
        mExecutor.purge()
    }

    /**
     * Same as [ThreadPoolExecutor.shutdown].
     */
    fun shutdown() {
        mExecutor.shutdown()
    }

    /**
     * Same as [ThreadPoolExecutor.shutdownNow].
     */
    fun shutdownNow(): List<Runnable> {
        return mExecutor.shutdownNow()
    }

    companion object {
        val instance = ThreadPoolUtils()
    }
}

fun main(args: Array<String>) {
    ThreadPoolUtils.instance.execute(Runnable { System.out.println("Hello Junk!") })
    ThreadPoolUtils.instance.execute(Runnable { })
}
