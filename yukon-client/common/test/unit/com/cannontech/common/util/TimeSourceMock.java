package com.cannontech.common.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class TimeSourceMock implements TimeSource {
    
    private AtomicLong currentTime = new AtomicLong(0);

    public TimeSourceMock(long startTime) {
        this.currentTime.set(startTime);
    }
    
    public TimeSourceMock(Date startTime) {
        this(startTime.getTime());
    }
    
    public TimeSourceMock() {
        this(new Date());
    }
    
    public long increment(long milliseconds) {
        return currentTime.addAndGet(milliseconds);
    }
    
    public long increment() {
        return currentTime.incrementAndGet();
    }

    @Override
    public long getCurrentMillis() {
        return currentTime.get();
    }

    @Override
    public Date getCurrentTime() {
        return new Date(getCurrentMillis());
    }

}
