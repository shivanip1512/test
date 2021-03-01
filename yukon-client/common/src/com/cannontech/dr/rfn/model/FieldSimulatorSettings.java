package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FieldSimulatorSettings implements Serializable {

    // % of duplicates to generate
    private int deviceConfigFailureRate;
    private String deviceGroup;

    public int getDeviceConfigFailureRate() {
        return deviceConfigFailureRate;
    }

    public void setDeviceConfigFailureRate(int deviceConfigFailureRate) {
        this.deviceConfigFailureRate = deviceConfigFailureRate;
    }

    public String getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
