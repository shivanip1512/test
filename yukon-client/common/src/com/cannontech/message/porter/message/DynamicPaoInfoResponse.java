package com.cannontech.message.porter.message;

import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponse {
    
    private int deviceID;
    private ImmutableMap<DynamicPaoInfoTimestampKeyEnum, Instant> timestampValues;
    private ImmutableMap<DynamicPaoInfoDurationKeyEnum, Duration> durationValues;

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
        return String.format("Response [deviceID=%s, timeValues=%s, durationValues=%s]", deviceID, timestampValues, durationValues);
    }

    public Map<DynamicPaoInfoTimestampKeyEnum, Instant> getTimestampValues() {
        return timestampValues;
    }
    public void setTimestampValues(Map<DynamicPaoInfoTimestampKeyEnum, Instant> timestampValues) {
        this.timestampValues = ImmutableMap.copyOf(timestampValues);
    }
    public Map<DynamicPaoInfoDurationKeyEnum, Duration> getDurationValues() {
        return durationValues;
    }
    public void setDurationValues(Map<DynamicPaoInfoDurationKeyEnum, Duration> durationValues) {
        this.durationValues = ImmutableMap.copyOf(durationValues);
    }
}