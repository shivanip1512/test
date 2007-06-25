package com.cannontech.common.device.groups.service;

public enum FixedDeviceGroups {
    BILLING("/Meters/Billing"),
    Collection("/Meters/Collection"),
    ;
    
    private final String prefix;

    private FixedDeviceGroups(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
}
