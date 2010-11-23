package com.cannontech.common.deviceReconfig.model;

public class DeviceReconfigMonitor {
    
    private int id;
    private String name;
    private int deviceCount;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getDeviceCount() {
        return deviceCount;
    }
    
    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }
    
}