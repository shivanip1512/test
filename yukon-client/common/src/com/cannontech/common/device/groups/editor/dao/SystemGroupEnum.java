package com.cannontech.common.device.groups.editor.dao;

public enum SystemGroupEnum {
    METERS("/Meters/"), 
    BILLING("/Meters/Billing/"), 
    COLLECTION("/Meters/Collection/"), 
    ALTERNATE("/Meters/Alternate/"), 
    FLAGS("/Meters/Flags/"), 
    INVENTORY("/Meters/Flags/Inventory/"), 
    DISCONNECTSTATUS("/Meters/Flags/DisconnectedStatus/"), 
    USAGEMONITORING("/Meters/Flags/UsageMonitoring/"),
    SYSTEM("/System/"),
    DEVICETYPES("/Device Types/"),
    ROUTES("/Routes/"),
    SCANNINGMETERS("/Scanning Meters/"),
    ;

    private String fullPath;

    SystemGroupEnum(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getFullPath() {
        return this.fullPath;
    }

}