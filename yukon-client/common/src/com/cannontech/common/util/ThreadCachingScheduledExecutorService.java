package com.cannontech.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ThreadCachingScheduledExecutorService extends ExecutorDelegate implements ScheduledExecutor {
    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadCachingScheduledExecutorService() {
        super(new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    private class RunnableProxy implements Runnable {
        private final Runnable proxied;
        private RunnableProxy(Runnable proxied) {
            this.proxied = proxied;
        }

        @Override
        public void run() {
            try {
                threadPoolExecutor.submit(proxied);
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(new RunnableProxy(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(new Callable<V>(){
            @SuppressWarnings("unchecked")
            @Override
            public V call() throws Exception {
                return (V) threadPoolExecutor.submit(callable);
            }}, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
                                                  TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(new RunnableProxy(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                                                     long delay, TimeUnit unit) {
        return scheduledExecutorService.scheduleWithFixedDelay(new RunnableProxy(command), initialDelay, delay, unit);
    }

    @Override
    @PreDestroy
    public void destroy() {
        // TODO:  perhaps these should try to shut down nicely first?
        scheduledExecutorService.shutdownNow();
    }
}
