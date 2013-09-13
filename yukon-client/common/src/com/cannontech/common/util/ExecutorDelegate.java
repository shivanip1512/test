package com.cannontech.common.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ExecutorDelegate implements Executor {
    protected final ThreadPoolExecutor threadPoolExecutor;

    public ExecutorDelegate(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void execute(Runnable command) {
        threadPoolExecutor.execute(command);
    }

    @PreDestroy
    public void destroy() {
        threadPoolExecutor.shutdownNow();
    }

    @ManagedAttribute
    public int getActiveCount() {
        return threadPoolExecutor.getActiveCount();
    }

    @ManagedAttribute
    public long getCompletedTaskCount() {
        return threadPoolExecutor.getCompletedTaskCount();
    }

    @ManagedAttribute
    public int getCorePoolSize() {
        return threadPoolExecutor.getCorePoolSize();
    }

    @ManagedAttribute
    public int getLargestPoolSize() {
        return threadPoolExecutor.getLargestPoolSize();
    }

    @ManagedAttribute
    public int getMaximumPoolSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }

    @ManagedAttribute
    public int getPoolSize() {
        return threadPoolExecutor.getPoolSize();
    }

    @ManagedAttribute
    public long getTaskCount() {
        return threadPoolExecutor.getTaskCount();
    }

    @ManagedAttribute
    public long getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }
}