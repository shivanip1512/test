package com.cannontech.common.smartNotification.model;

import org.joda.time.Instant;

public class SmartNotificationEventData {
    private Instant timestamp;
    private int monitorId;
    private int deviceId;
    private String status;
    private String type;
    private String deviceName;
    
    public int getMonitorId() {
        return monitorId;
    }
    
    public void setMonitorId(int monitorId) {
        this.monitorId = monitorId;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
