package com.cannontech.dr.rfn.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

public class RfnLcrDataSimulatorStatus {
    
    private volatile Instant startTime;
    private volatile Instant stopTime;
    private AtomicLong success = new AtomicLong(0);
    private AtomicLong failure = new AtomicLong(0);
    private AtomicBoolean running = new AtomicBoolean(false);
    
    private volatile Instant lastInjectionTime;

    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public AtomicLong getSuccess() {
        return success;
    }
    public void setSuccess(AtomicLong success) {
        this.success = success;
    }
    public Instant getLastInjectionTime() {
        return lastInjectionTime;
    }
    public void setLastInjectionTime(Instant lastInjectionTime) {
        this.lastInjectionTime = lastInjectionTime;
    }
    public AtomicLong getFailure() {
        return failure;
    }
    public void setFailure(AtomicLong failure) {
        this.failure = failure;
    }

    public AtomicBoolean isRunning() {
        return running;
    }
    public void setRunning(AtomicBoolean running) {
        this.running = running;
    }
    public Instant getStopTime() {
        return stopTime;
    }
    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }
}
