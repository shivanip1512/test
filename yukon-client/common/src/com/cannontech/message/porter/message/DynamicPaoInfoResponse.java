package com.cannontech.message.porter.message;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Duration;
import org.joda.time.Instant;
import com.google.common.collect.ImmutableMap;

public class DynamicPaoInfoResponse {
    
    private int deviceID;
    private ImmutableMap<DynamicPaoInfoTimestampKeyEnum, Instant> timestampValues;
    private ImmutableMap<DynamicPaoInfoDurationKeyEnum, Duration> durationValues;
    private ImmutableMap<DynamicPaoInfoPercentageKeyEnum, Double> percentageValues;

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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

    public Map<DynamicPaoInfoPercentageKeyEnum, Double> getPercentageValues() {
        return percentageValues;
    }
    public void setPercentageValues(Map<DynamicPaoInfoPercentageKeyEnum, Double> percentageValues) {
        this.percentageValues = ImmutableMap.copyOf(percentageValues);
    }
}