package com.cannontech.web.support.development.database.objects;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class DevObject {
    private boolean create = true;
    private AtomicInteger successCount = new AtomicInteger();
    private AtomicInteger failureCount = new AtomicInteger();

    public boolean isCreate() {
        return create;
    }
    public void setCreate(boolean create) {
        this.create = create;
    }
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
