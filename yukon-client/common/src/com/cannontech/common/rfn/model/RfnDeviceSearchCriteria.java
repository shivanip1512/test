package com.cannontech.common.rfn.model;

public class RfnDeviceSearchCriteria {
    private String name;
    private String serialNumber;
    
    public RfnDeviceSearchCriteria (String name, String serialNumber) {
        this.name = name;
        this.serialNumber = serialNumber;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
