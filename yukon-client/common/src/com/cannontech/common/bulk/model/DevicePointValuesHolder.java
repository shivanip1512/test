package com.cannontech.common.bulk.model;

import java.util.List;

import com.google.common.collect.Lists;

public class DevicePointValuesHolder {
    private String deviceName;
    private List<Double> pointValues = Lists.newArrayList();
    
    public DevicePointValuesHolder(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public List<Double> getPointValues() {
        return pointValues;
    }
    
    public void setPointValues(List<Double> pointValues) {
        this.pointValues = pointValues;
    }
}
