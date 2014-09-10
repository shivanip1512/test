package com.cannontech.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * An scheduled executor with a dynamically sized thread pool. Threads are created as needed and unused
 * threads will
 * be cleaned up after 60 seconds of inactivity.
 */
@ManagedResource
public class ThreadCachingScheduledExecutorService extends ExecutorDelegate implements ScheduledExecutor {
    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadCachingScheduledExecutorService(String identifier) {
        super(new NamedThreadPoolExecutor(identifier, 0, Integer.MAX_VALUE, new SynchronousQueue<Runnable>()));
        scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(identifier + "-scheduler"));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(new RunnableProxy(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(new Callable<V>() {
            @SuppressWarnings("unchecked")
            @Override
            public V call() throws Exception {
                return (V) threadPoolExecutor.submit(callable);
            }
        }, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(new RunnableProxy(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduledExecutorService.scheduleWithFixedDelay(new RunnableProxy(command), initialDelay, delay, unit);
    }

    @Override
    @PreDestroy
    public void destroy() {
        scheduledExecutorService.shutdownNow();
    }

    private class RunnableProxy implements Runnable {
        private final Runnable proxied;

        private RunnableProxy(Runnable proxied) {
            this.proxied = proxied;
        }

        @Override
        public void run() {
            threadPoolExecutor.submit(proxied);
        }
    }
}
