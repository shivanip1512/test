package com.cannontech.multispeak.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

public class MultispeakDeviceGroupSyncProgress {

    private MultispeakSyncType type;
    private MultispeakDeviceGroupSyncProgressStatus status;
    private AtomicInteger metersProcessed = new AtomicInteger(0);
    private Map<MultispeakSyncTypeProcessorType, AtomicInteger> changeCount = Maps.newHashMap();
    private Map<MultispeakSyncTypeProcessorType, AtomicInteger> noChangeCount = Maps.newHashMap();
    private Exception exception = null;
    
    // type
    public MultispeakDeviceGroupSyncProgress(MultispeakSyncType type) {
        this.type = type;
        this.status = MultispeakDeviceGroupSyncProgressStatus.RUNNING;
    }
    
    public MultispeakSyncType getType() {
        return type;
    }
    
    public MultispeakDeviceGroupSyncProgressStatus getStatus() {
        return status;
    }
    
    // meters processed
    public void incrementMetersProcessedCount() {
        metersProcessed.incrementAndGet();
    }
    public int getMetersProcessedCount() {
        return metersProcessed.get();
    }
    
    public void incrementChangeCount(MultispeakSyncTypeProcessorType processorType) {
        
        if (!changeCount.containsKey(processorType)) {
            changeCount.put(processorType, new AtomicInteger(0));
        }
        changeCount.get(processorType).incrementAndGet();
    }
    
    public void incrementNoChangeCount(MultispeakSyncTypeProcessorType processorType) {
        
        if (!noChangeCount.containsKey(processorType)) {
            noChangeCount.put(processorType, new AtomicInteger(0));
        }
        noChangeCount.get(processorType).incrementAndGet();
    }
    
    public int getChangeCount(MultispeakSyncTypeProcessorType processorType) {
        
        if (!changeCount.containsKey(processorType)) {
            return 0;
        }
        return changeCount.get(processorType).get();
    }
    
    public int getNoChangeCount(MultispeakSyncTypeProcessorType processorType) {
        
        if (!noChangeCount.containsKey(processorType)) {
            return 0;
        }
        return noChangeCount.get(processorType).get();
    }
    
    // status
    public boolean isRunning() {
        return this.status == MultispeakDeviceGroupSyncProgressStatus.RUNNING;
    }
    
    public void finish() {
        this.status = MultispeakDeviceGroupSyncProgressStatus.FINISHED;
    }
    public boolean isFinished() {
        return this.status == MultispeakDeviceGroupSyncProgressStatus.FINISHED;
    }
    
    public void cancel() {
        this.status = MultispeakDeviceGroupSyncProgressStatus.CANCELED;
    }
    public boolean isCanceled() {
        return this.status == MultispeakDeviceGroupSyncProgressStatus.CANCELED;
    }
    
    // exception
    public void setException(Exception exception) {
        
        if (exception != null) {
            this.exception = exception;
            this.status = MultispeakDeviceGroupSyncProgressStatus.FAILED;
        }
    }
    public Exception getException() {
        return this.exception;
    }
    public boolean isHasException() {
        return this.exception != null;
    }
}
