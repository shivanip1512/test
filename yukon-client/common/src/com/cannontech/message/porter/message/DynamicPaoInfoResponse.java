package com.cannontech.message.porter.message;

import java.util.Map;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponse {
    
    private int deviceID;
    private ImmutableMap<DynamicPaoInfoKeyEnum, Instant> timeValues;
    private ImmutableMap<DynamicPaoInfoKeyEnum, String> stringValues;
    private ImmutableMap<DynamicPaoInfoKeyEnum, Long> longValues;

    public DynamicPaoInfoResponse() { }
    
    public DynamicPaoInfoResponse(int deviceID) {
        this.deviceID = deviceID;
    }
    
    public int getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
    
    @Override
    public String toString() {
        return String.format("Response [deviceID=%s, timeResults=%s, stringResults=%s, longResults=%s]", deviceID, timeValues, stringValues, longValues);
    }

    public Map<DynamicPaoInfoKeyEnum, Instant> getTimeValues() {
        return timeValues;
    }
    public void setTimeValues(Map<DynamicPaoInfoKeyEnum, Instant> timeValues) {
        this.timeValues = ImmutableMap.copyOf(timeValues);
    }
    public Map<DynamicPaoInfoKeyEnum, String> getStringValues() {
        return stringValues;
    }
    public void setStringValues(Map<DynamicPaoInfoKeyEnum, String> stringValues) {
        this.stringValues = ImmutableMap.copyOf(stringValues);
    }
    public Map<DynamicPaoInfoKeyEnum, Long> getLongValues() {
        return longValues;
    }
    public void setLongValues(Map<DynamicPaoInfoKeyEnum, Long> longValues) {
        this.longValues = ImmutableMap.copyOf(longValues);
    }
}