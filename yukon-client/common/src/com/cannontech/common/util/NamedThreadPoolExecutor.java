package com.cannontech.common.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NamedThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Creates a new {@code ThreadPoolExecutor} with threads named with the given identifier.
     * 
     * Defaults the thread keepAliveTime to 60 seconds and the workQueue to a unbounded LinkedBlockingQueue
     *
     * @param corePoolSize the number of threads to keep in the pool, even if they are idle, unless
     *        {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     */
    public NamedThreadPoolExecutor(String identifier, int corePoolSize, int maximumPoolSize) {
        this(identifier, corePoolSize, maximumPoolSize, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with threads named with the given identifier.
     * 
     * Defaults the thread keepAliveTime to 60 seconds
     *
     * @param corePoolSize the number of threads to keep in the pool, even if they are idle, unless
     *        {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     */
    public NamedThreadPoolExecutor(String identifier, int corePoolSize, int maximumPoolSize,
            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, workQueue, new NamedThreadFactory(identifier));
    }
}
