package com.cannontech.common.device.groups.service;

public enum FixedDeviceGroups {
    COLLECTIONGROUP("/Meters/Collection"),
    TESTCOLLECTIONGROUP("/Meters/Alternate"),
    BILLINGGROUP("/Meters/Billing"),
    CUSTOM1GROUP("/Meters/CustomGroup1"),
    CUSTOM2GROUP("/Meters/CustomGroup2"),
    CUSTOM3GROUP("/Meters/CustomGroup3"),
    ;
    
    private final String prefix;

    private FixedDeviceGroups(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getGroup(String group) {
        return getPrefix() + "/" + group;
    }
    
    public static FixedDeviceGroups resolveGroup(String columnName) {
        return valueOf(columnName.toUpperCase());
    }
}
