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
    DEVICETYPES("/System/Device Types/"),
    ROUTES("/System/Routes/"),
    SCANNINGMETERS("/System/Meters/Scanning/"),
    TEMPORARYGROUPS("/System/Temporary/"),
    SYSTEMMETERS("/System/Meters/"),
    SYSTEMMETERSDISABLED("/System/Meters/Disabled/"),
    ;

    private String fullPath;

    SystemGroupEnum(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getFullPath() {
        return this.fullPath;
    }

}