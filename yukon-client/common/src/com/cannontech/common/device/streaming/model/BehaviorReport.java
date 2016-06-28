package com.cannontech.common.device.streaming.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;

public class BehaviorReport {
    private int id;
    private int deviceId;
    private BehaviorReportStatus status;
    private BehaviorType type; 
    private Instant timestamp;
    private List<BehaviorValue> values = new ArrayList<>();
    
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
    public BehaviorType getType() {
        return type;
    }
    public void setType(BehaviorType type) {
        this.type = type;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public List<BehaviorValue> getValues() {
        return values;
    }
    public void setValues(List<BehaviorValue> values) {
        this.values = values;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    } 
}
