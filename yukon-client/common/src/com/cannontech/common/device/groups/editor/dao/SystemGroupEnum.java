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
    DEVICECONFIGS("/System/Meters/Device Configs/"),
    ROUTES("/System/Routes/"),
    SCANNINGMETERS("/System/Meters/Scanning/"),
    TEMPORARYGROUPS("/System/Temporary/"),
    SYSTEMMETERS("/System/Meters/"),
    SYSTEMMETERSDISABLED("/System/Meters/Disabled/"),
    DEVICETAGS("/System/Device Tags/"),
    OUTAGE_PROCESSING("/Meters/Monitors/Outage/"),
    TAMPER_FLAG_PROCESSING("/Meters/Monitors/Tamper Flag/"),
    SYSTEMMETERS_DISCONNECT("/System/Meters/Disconnect/"),
    ;

    private String fullPath;

    SystemGroupEnum(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getFullPath() {
        return this.fullPath;
    }

}