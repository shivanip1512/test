package com.cannontech.web.dev.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class RphSimulatorPointStatus {
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isCompleted;

    private volatile String errorMessage;


    public RphSimulatorPointStatus() {
        this.isRunning = new AtomicBoolean();
        this.isCompleted = new AtomicBoolean();
    }

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AtomicBoolean getIsCompleted() {
        return isCompleted;
    }
}