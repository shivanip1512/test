package com.cannontech.multispeak.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

public class MultispeakDeviceGroupSyncProgress extends MultispeakSyncProcessBase {

    private AtomicInteger metersProcessed = new AtomicInteger(0);
    private Map<MultispeakSyncTypeProcessorType, AtomicInteger> changeCount = Maps.newHashMap();
    private Map<MultispeakSyncTypeProcessorType, AtomicInteger> noChangeCount = Maps.newHashMap();

    public MultispeakDeviceGroupSyncProgress(MultispeakSyncType type) {
        super(type);
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
}
