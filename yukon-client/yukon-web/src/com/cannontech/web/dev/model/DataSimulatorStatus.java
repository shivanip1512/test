package com.cannontech.web.dev.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

public class DataSimulatorStatus {
    private final AtomicBoolean isRunning6200;
    private final AtomicBoolean isCancelled6200;
    private final AtomicLong numComplete6200;

    private volatile Instant lastFinishedInjection6200;
    private volatile long numTotal6200;
    private volatile String errorMessage;

    private final AtomicBoolean isRunning6600;
    private final AtomicBoolean isCancelled6600;
    private final AtomicLong numComplete6600;

    private volatile Instant lastFinishedInjection6600;
    private volatile long numTotal6600;

    public DataSimulatorStatus() {
        this.isRunning6200 = new AtomicBoolean();
        this.isCancelled6200 = new AtomicBoolean();
        this.numComplete6200 = new AtomicLong();
        this.numTotal6200 = 0;

        this.isRunning6600 = new AtomicBoolean();
        this.isCancelled6600 = new AtomicBoolean();
        this.numComplete6600 = new AtomicLong();
        this.numTotal6600 = 0;
    }

    public Instant getLastFinishedInjection6200() {
        return lastFinishedInjection6200;
    }

    public void setLastFinishedInjection6200(Instant lastFinishedInjection6200) {
        this.lastFinishedInjection6200 = lastFinishedInjection6200;
    }

    public long getNumTotal6200() {
        return numTotal6200;
    }

    public void setNumTotal6200(long numTotal6200) {
        this.numTotal6200 = numTotal6200;
    }

    public void setIsRunning6200(boolean isRunning) {
        this.isRunning6200.set(isRunning);
    }

    public void setIsRunning6600(boolean isRunning) {
        this.isRunning6600.set(isRunning);
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getLastFinishedInjection6600() {
        return lastFinishedInjection6600;
    }

    public void setLastFinishedInjection6600(Instant lastFinishedInjection6600) {
        this.lastFinishedInjection6600 = lastFinishedInjection6600;
    }

    public long getNumTotal6600() {
        return numTotal6600;
    }

    public void setNumTotal6600(long numTotal6600) {
        this.numTotal6600 = numTotal6600;
    }

    public AtomicBoolean getIsRunning6200() {
        return isRunning6200;
    }

    public AtomicBoolean getIsCancelled6200() {
        return isCancelled6200;
    }

    public AtomicLong getNumComplete6200() {
        return numComplete6200;
    }

    public AtomicBoolean getIsRunning6600() {
        return isRunning6600;
    }

    public AtomicBoolean getIsCancelled6600() {
        return isCancelled6600;
    }

    public AtomicLong getNumComplete6600() {
        return numComplete6600;
    }

}