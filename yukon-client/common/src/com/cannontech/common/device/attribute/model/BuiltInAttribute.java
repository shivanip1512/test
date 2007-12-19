package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE("Load Profile"), 
    ENERGY("Energy Reading"), 
    TOU_RATE_A_ENERGY("Tou Rate A Energy"), 
    TOU_RATE_B_ENERGY("Tou Rate B Energy"), 
    TOU_RATE_C_ENERGY("Tou Rate C Energy"), 
    TOU_RATE_D_ENERGY("Tou Rate D Energy"), 
    DEMAND("Demand"), 
    PEAK_DEMAND("Peak Demand"),
    TOU_RATE_A_PEAK_DEMAND("Tou Rate A Peak"),
    TOU_RATE_B_PEAK_DEMAND("Tou Rate B Peak"),
    TOU_RATE_C_PEAK_DEMAND("Tou Rate C Peak"),
    TOU_RATE_D_PEAK_DEMAND("Tou Rate D Peak"),
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
