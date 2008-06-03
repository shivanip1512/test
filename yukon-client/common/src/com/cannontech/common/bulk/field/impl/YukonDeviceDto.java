package com.cannontech.common.bulk.field.impl;

public class YukonDeviceDto {
    
    // DO NOT ADD AN IDENTIFIER TO THIS CLASS
    
    private Integer address;
    private Boolean enable;
    private String meterNumber;
    private String name;
    private String route; 
    private Integer substation;
    private Integer deviceType;
    
    
    // SETTER GETTERS
    public Integer getAddress() {
        return address;
    }
    public void setAddress(Integer address) {
        this.address = address;
    }
    public String getMeterNumber() {
        return meterNumber;
    }
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getSubstation() {
        return substation;
    }
    public void setSubstation(Integer substation) {
        this.substation = substation;
    }
    public Boolean getEnable() {
        return enable;
    }
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
    public Integer getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
}
