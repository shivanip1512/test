package com.cannontech.common.device.groups.editor.dao;

public enum SystemGroupEnum {
    METERS("/Meters/"), 
    BILLING("/Meters/Billing/"), 
    COLLECTION("/Meters/Collection/"), 
    ALTERNATE("/Meters/Alternate/"), 
    CUSTOM_GROUP_ONE("/Meters/CustomGroup1/"), 
    CUSTOM_GROUP_TWO("/Meters/CustomGroup2/"), 
    CUSTOM_GROUP_THREE("/Meters/CustomGroup3/"), 
    FLAGS("/Meters/Flags/"), 
    INVENTORY("/Meters/Flags/Inventory/"), 
    DISCONNECTSTATUS("/Meters/Flags/DisconnectedStatus/"), 
    USAGEMONITORING("/Meters/Flags/UsageMonitoring/");

    private String fullPath;

    SystemGroupEnum(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getFullPath() {
        return this.fullPath;
    }

}