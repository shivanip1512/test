package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE("Load Profile"), 
    USAGE("Usage"), 
    TOU_RATE_A_USAGE("Tou Rate A Usage"), 
    TOU_RATE_B_USAGE("Tou Rate B Usage"), 
    TOU_RATE_C_USAGE("Tou Rate C Usage"), 
    TOU_RATE_D_USAGE("Tou Rate D Usage"), 
    DEMAND("Demand"), 
    PEAK_DEMAND("Peak Demand"),
    TOU_RATE_A_PEAK("Tou Rate A Peak"),
    TOU_RATE_B_PEAK("Tou Rate B Peak"),
    TOU_RATE_C_PEAK("Tou Rate C Peak"),
    TOU_RATE_D_PEAK("Tou Rate D Peak"),
    BLINK_COUNT("Blink Count"), 
    DISCONNECT_STATUS("Disconnect Status"),
    OUTAGE_LOG("Outage Log"),
    VOLTAGE("Voltage"), 
    VOLTAGE_PROFILE("Voltage Profile"),
    KVAR("kVAr"),
    KVARH("kVArh");
    
    private BuiltInAttribute(String description) {
        this.description = description;
    }
    
    private String description;
    
    public String getDescription() {
        return description;
    }
    
    public String getKey() {
        return this.name();
    }
}
