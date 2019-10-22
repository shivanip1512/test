package com.cannontech.message.porter.message;

import java.util.Collection;
import java.util.EnumSet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DynamicPaoInfoRequest {
    
    private int deviceID;
    private EnumSet<DynamicPaoInfoDurationKeyEnum> durationKeys = EnumSet.noneOf(DynamicPaoInfoDurationKeyEnum.class);
    private EnumSet<DynamicPaoInfoTimestampKeyEnum> timestampKeys = EnumSet.noneOf(DynamicPaoInfoTimestampKeyEnum.class);
    private EnumSet<DynamicPaoInfoPercentageKeyEnum> percentageKeys = EnumSet.noneOf(DynamicPaoInfoPercentageKeyEnum.class);

    public DynamicPaoInfoRequest(int deviceID) {
        this.deviceID = deviceID;
    }
    
    public int getDeviceID() {
        return deviceID;
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
    
    public EnumSet<DynamicPaoInfoPercentageKeyEnum> getPercentageKeys() {
        return percentageKeys;
    }

    public void setPercentageKeys(Collection<DynamicPaoInfoPercentageKeyEnum> percentageKeys) {
        this.percentageKeys = EnumSet.copyOf(percentageKeys);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}