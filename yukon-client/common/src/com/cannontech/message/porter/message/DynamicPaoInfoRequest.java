package com.cannontech.message.porter.message;

import java.util.Collection;
import java.util.EnumSet;

public class DynamicPaoInfoRequest {
    
    private int deviceID;
    private EnumSet<DynamicPaoInfoDurationKeyEnum> durationKeys;
    private EnumSet<DynamicPaoInfoTimestampKeyEnum> timestampKeys;

    public DynamicPaoInfoRequest() { }
    
    public int getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
    
    public Collection<DynamicPaoInfoDurationKeyEnum> getDurationKeys() {
        return durationKeys;
    }
    
    public void setDurationKeys(Collection<DynamicPaoInfoDurationKeyEnum> durationKeys) {
        this.durationKeys = EnumSet.copyOf(durationKeys);
    }
    
    public Collection<DynamicPaoInfoTimestampKeyEnum> getTimestampKeys() {
        return timestampKeys;
    }
    
    public void setTimestampKeys(Collection<DynamicPaoInfoTimestampKeyEnum> timestampKeys) {
        this.timestampKeys = EnumSet.copyOf(timestampKeys);
    }
    
    @Override
    public String toString() {
        return String.format("Request [deviceID=%s, durationKeys=%s, timestampKeys=%s]", deviceID, durationKeys, timestampKeys);
    }
    
}