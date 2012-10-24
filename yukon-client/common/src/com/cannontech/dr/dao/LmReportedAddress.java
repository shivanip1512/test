package com.cannontech.dr.dao;

import java.io.Serializable;

import org.joda.time.Instant;

public abstract class LmReportedAddress implements Serializable {

    protected int changeId;
    protected int deviceId;
    protected Instant timestamp;
    
    public int getChangeId() {
        return changeId;
    }
    
    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
}