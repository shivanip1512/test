package com.cannontech.dr.rfn.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.Instant;

public class RfnLcrDataSimulatorStatus {
    private volatile Instant lastFinishedInjection6200;
    private AtomicLong numComplete6200;
    private volatile String errorMessage;
    private AtomicBoolean isRunning6200;

    private volatile Instant lastFinishedInjection6600;
    private AtomicLong numComplete6600;
    private AtomicBoolean isRunning6600;

    public RfnLcrDataSimulatorStatus() {
        this.numComplete6200 = new AtomicLong();
        this.isRunning6200 = new AtomicBoolean();
        this.numComplete6600 = new AtomicLong();
        this.isRunning6600 = new AtomicBoolean();
    }

    public static void reInitializeStatus(RfnLcrDataSimulatorStatus status) {
        status.numComplete6200 = new AtomicLong();
        status.isRunning6200 = new AtomicBoolean();
        status.numComplete6600 = new AtomicLong();
        status.isRunning6600 = new AtomicBoolean();
        status.errorMessage = null;
    }
    public Instant getLastFinishedInjection6200() {
        return lastFinishedInjection6200;
    }

    public void setLastFinishedInjection6200(Instant lastFinishedInjection6200) {
        this.lastFinishedInjection6200 = lastFinishedInjection6200;
    }

    public Instant getLastFinishedInjection6600() {
        return lastFinishedInjection6600;
    }

    public void setLastFinishedInjection6600(Instant lastFinishedInjection6600) {
        this.lastFinishedInjection6600 = lastFinishedInjection6600;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AtomicLong getNumComplete6200() {
        return numComplete6200;
    }

    public AtomicBoolean getIsRunning6200() {
        return isRunning6200;
    }

    public AtomicLong getNumComplete6600() {
        return numComplete6600;
    }

    public AtomicBoolean getIsRunning6600() {
        return isRunning6600;
    }

    public void resetCompletionState() {
        this.numComplete6200 = new AtomicLong();
        this.numComplete6600 = new AtomicLong();
    }

}
