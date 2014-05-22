package com.cannontech.web.dev.database.objects;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class DevObject {
    private AtomicInteger successCount = new AtomicInteger();
    private AtomicInteger failureCount = new AtomicInteger();

    public int getSuccessCount() {
        return successCount.intValue();
    }
    
    public void setSuccessCount(AtomicInteger successCount) {
        this.successCount = successCount;
    }

    public void incrementSuccessCount() {
        successCount.incrementAndGet();
    }

    public int getFailureCount() {
        return failureCount.intValue();
    }

    public void setFailureCount(AtomicInteger failureCount) {
        this.failureCount = failureCount;
    }
    
    public void addToFailureCount(int num) {
        this.failureCount.addAndGet(num);
    }
    
    public void incrementFailureCount() {
        failureCount.incrementAndGet();
    }
    public abstract int getTotal();
}
