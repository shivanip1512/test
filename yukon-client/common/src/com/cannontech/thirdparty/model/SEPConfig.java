package com.cannontech.thirdparty.model;

import org.joda.time.Instant;

public class SEPConfig {
    
    private int deviceId;
    private Instant timestamp;
    private int untilityEnrollmentGroup;
    private int rampStartTimeMinutes;
    private int rampStopTimeMinutes;
    private int deviceClass;
    

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
    public int getUntilityEnrollmentGroup() {
        return untilityEnrollmentGroup;
    }
    public void setUntilityEnrollmentGroup(int untilityEnrollmentGroup) {
        this.untilityEnrollmentGroup = untilityEnrollmentGroup;
    }
    public int getRampStartTimeMinutes() {
        return rampStartTimeMinutes;
    }
    public void setRampStartTimeMinutes(int rampStartTimeMinutes) {
        this.rampStartTimeMinutes = rampStartTimeMinutes;
    }
    public int getRampStopTimeMinutes() {
        return rampStopTimeMinutes;
    }
    public void setRampStopTimeMinutes(int rampStopTimeMinutes) {
        this.rampStopTimeMinutes = rampStopTimeMinutes;
    }
    public int getDeviceClass() {
        return deviceClass;
    }
    public void setDeviceClass(int deviceClass) {
        this.deviceClass = deviceClass;
    }
}
