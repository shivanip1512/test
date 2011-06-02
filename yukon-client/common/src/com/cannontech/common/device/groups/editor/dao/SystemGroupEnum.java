package com.cannontech.common.device.groups.editor.dao;

public enum SystemGroupEnum {
    ROOT("/"),
    METERS("/Meters/"), 
    BILLING("/Meters/Billing/"), 
    COLLECTION("/Meters/Collection/"), 
    ALTERNATE("/Meters/Alternate/"), 
    FLAGS("/Meters/Flags/"), 
    INVENTORY("/Meters/Flags/Inventory/"), 
    DISCONNECTSTATUS("/Meters/Flags/DisconnectedStatus/"), 
    USAGEMONITORING("/Meters/Flags/UsageMonitoring/"),
    SYSTEM("/System/"),
    ATTRIBUTES_EXISTING("/System/Attributes/Existing"),
    ATTRIBUTES_SUPPORTED("/System/Attributes/Supported"),
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
    SUBSTATION_NAME("/Meters/CIS Substation/"),
    PHASE_DETECT_LAST_RESULTS_A("/System/Meters/Phase Detect/Last Results/A"),
    PHASE_DETECT_LAST_RESULTS_B("/System/Meters/Phase Detect/Last Results/B"),
    PHASE_DETECT_LAST_RESULTS_C("/System/Meters/Phase Detect/Last Results/C"),
    PHASE_DETECT_LAST_RESULTS_AB("/System/Meters/Phase Detect/Last Results/AB"),
    PHASE_DETECT_LAST_RESULTS_AC("/System/Meters/Phase Detect/Last Results/AC"),
    PHASE_DETECT_LAST_RESULTS_BC("/System/Meters/Phase Detect/Last Results/BC"),
    PHASE_DETECT_LAST_RESULTS_ABC("/System/Meters/Phase Detect/Last Results/ABC"),
    PHASE_DETECT_LAST_RESULTS_UNKNOWN("/System/Meters/Phase Detect/Last Results/UNKNOWN"),
    ;

    private String fullPath;

    SystemGroupEnum(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getFullPath() {
        return this.fullPath;
    }

}