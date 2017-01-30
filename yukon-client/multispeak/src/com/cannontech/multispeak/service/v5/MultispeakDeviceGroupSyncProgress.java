package com.cannontech.multispeak.service.v5;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

public class MultispeakDeviceGroupSyncProgress {
    private MultispeakDeviceGroupSyncType type;
    private MultispeakDeviceGroupSyncProgressStatus status;
    private Map<MultispeakDeviceGroupSyncTypeProcessorType, AtomicInteger> changeCount = Maps.newHashMap();
    private Map<MultispeakDeviceGroupSyncTypeProcessorType, AtomicInteger> noChangeCount = Maps.newHashMap();
    private AtomicInteger metersProcessed = new AtomicInteger(0);
    private Exception exception = null;

    // type
    public MultispeakDeviceGroupSyncProgress(MultispeakDeviceGroupSyncType type) {
        this.type = type;
        this.status = MultispeakDeviceGroupSyncProgressStatus.RUNNING;
    }

    public boolean isCanceled() {
        return this.status == MultispeakDeviceGroupSyncProgressStatus.CANCELED;
    }

    public void finish() {
        this.status = MultispeakDeviceGroupSyncProgressStatus.FINISHED;
    }

    public void incrementChangeCount(MultispeakDeviceGroupSyncTypeProcessorType processorType) {

        if (!changeCount.containsKey(processorType)) {
            changeCount.put(processorType, new AtomicInteger(0));
        }
        changeCount.get(processorType).incrementAndGet();
    }

    public void incrementNoChangeCount(MultispeakDeviceGroupSyncTypeProcessorType processorType) {

        if (!noChangeCount.containsKey(processorType)) {
            noChangeCount.put(processorType, new AtomicInteger(0));
        }
        noChangeCount.get(processorType).incrementAndGet();
    }

    // meters processed
    public void incrementMetersProcessedCount() {
        metersProcessed.incrementAndGet();
    }

    // exception
    public void setException(Exception exception) {
        if (exception != null) {
            this.exception = exception;
            this.status = MultispeakDeviceGroupSyncProgressStatus.FAILED;
        }
    }

}
