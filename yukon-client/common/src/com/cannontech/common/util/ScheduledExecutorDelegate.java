package com.cannontech.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorDelegate implements ScheduledExecutor {
    private ScheduledExecutorService service;

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return service.schedule(command, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return service.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
                                                  TimeUnit unit) {
        return service.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                                                     long delay, TimeUnit unit) {
        return service.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public void execute(Runnable command) {
        service.execute(command);
    }
    
    public void shutdown() {
        service.shutdownNow();
    }

    public void setService(ScheduledExecutorService service) {
        this.service = service;
    }

}
