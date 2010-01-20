package com.cannontech.common.pao.attribute.model;

public enum BuiltInAttribute implements Attribute {
    BLINK_COUNT("Blink Count"), 
    DEMAND("Demand"), 
    DISCONNECT_STATUS("Disconnect Status"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    KVAR("kVAr"),
    KVARH("kVArh"),
    LOAD_PROFILE("Load Profile"), 
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    OUTAGE_LOG("Outage Log"),
    PEAK_DEMAND("Peak Demand"),
    PHASE("Phase"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    TAMPER_FLAG("Tamper Flag"),
    TOU_RATE_A_PEAK_DEMAND("Tou Rate A Peak"),
    TOU_RATE_B_PEAK_DEMAND("Tou Rate B Peak"),
    TOU_RATE_C_PEAK_DEMAND("Tou Rate C Peak"),
    TOU_RATE_D_PEAK_DEMAND("Tou Rate D Peak"),
    TOU_RATE_A_USAGE("Tou Rate A Usage"), 
    TOU_RATE_B_USAGE("Tou Rate B Usage"), 
    TOU_RATE_C_USAGE("Tou Rate C Usage"), 
    TOU_RATE_D_USAGE("Tou Rate D Usage"), 
    USAGE("Usage Reading"), 
    VOLTAGE("Voltage"), 
    VOLTAGE_PROFILE("Voltage Profile"),
    ZERO_USAGE_FLAG("Zero Usage Flag"),
    ;
    
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