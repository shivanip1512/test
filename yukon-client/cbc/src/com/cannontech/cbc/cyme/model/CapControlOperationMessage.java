package com.cannontech.cbc.cyme.model;

import java.io.Serializable;

import org.joda.time.Instant;

public class CapControlOperationMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int deviceId;
    private CapControlOperation operation;
    private Instant timestamp;
    
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public CapControlOperation getOperation() {
        return operation;
    }
    public void setOperation(CapControlOperation operation) {
        this.operation = operation;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
