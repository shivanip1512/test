package com.cannontech.web.dev.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

import com.cannontech.common.pao.attribute.model.Attribute;

public class PointInjectionStatus {
    private final AtomicLong throttlePerSecond;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isCanceled;
    private final AtomicLong numComplete;

    private volatile Instant lastFinishedInjection;
    private volatile long numTotal;
    private volatile String deviceGroupName;
    private volatile String errorMessage;
    private volatile Attribute attribute;

    public PointInjectionStatus() {
        this.throttlePerSecond = new AtomicLong();
        this.isRunning = new AtomicBoolean();
        this.isCanceled = new AtomicBoolean();
        this.numComplete = new AtomicLong();
        this.numTotal = 0;
    }

    public AtomicLong getThrottlePerSecond() {
        return throttlePerSecond;
    }

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    public AtomicBoolean getIsCanceled() {
        return isCanceled;
    }

    public AtomicLong getNumComplete() {
        return numComplete;
    }

    public long getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(long numTotal) {
        this.numTotal = numTotal;
    }

    public String getDeviceGroupName() {
        return deviceGroupName;
    }

    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Instant getLastFinishedInjection() {
        return lastFinishedInjection;
    }

    public void setLastFinishedInjection(Instant lastFinishedInjection) {
        this.lastFinishedInjection = lastFinishedInjection;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}