package com.cannontech.common.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ExecutorDelegate implements Executor {

    private final ExecutorService service;

    public ExecutorDelegate(ExecutorService service) {
        this.service = service;
    }

    public void execute(Runnable command) {
        service.execute(command);
    }

    @PreDestroy
    public void shutdown() {
        service.shutdownNow();
    }

    @ManagedAttribute
    public int getActiveCount() {
        return ((ScheduledThreadPoolExecutor)service).getActiveCount() ;
    }

    @ManagedAttribute
    public long getCompletedTaskCount() {
        return ((ScheduledThreadPoolExecutor)service).getCompletedTaskCount() ;
    }

    @ManagedAttribute
    public int getCorePoolSize() {
        return ((ScheduledThreadPoolExecutor)service).getCorePoolSize() ;
    }

    @ManagedAttribute
    public int getLargestPoolSize() {
        return ((ScheduledThreadPoolExecutor)service).getLargestPoolSize() ;
    }

    @ManagedAttribute
    public int getMaximumPoolSize() {
        return ((ScheduledThreadPoolExecutor)service).getMaximumPoolSize();
    }

    @ManagedAttribute
    public int getPoolSize() {
        return ((ScheduledThreadPoolExecutor)service).getPoolSize();
    }

    @ManagedAttribute
    public long getTaskCount() {
        return ((ScheduledThreadPoolExecutor)service).getTaskCount();
    }

    @ManagedAttribute
    public long getQueueSize() {
        return ((ScheduledThreadPoolExecutor)service).getQueue().size();
    }

}