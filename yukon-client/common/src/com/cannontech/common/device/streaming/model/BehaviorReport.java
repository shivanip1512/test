package com.cannontech.common.device.streaming.model;

import org.joda.time.Instant;

public class BehaviorReport extends Behavior{
    private int deviceId;
    private BehaviorReportStatus status;
    private Instant timestamp;
    
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public BehaviorReportStatus getStatus() {
        return status;
    }
    public void setStatus(BehaviorReportStatus status) {
        this.status = status;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
