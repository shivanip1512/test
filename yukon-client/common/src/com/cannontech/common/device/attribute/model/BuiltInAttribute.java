package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE("Load Profile"), 
    USAGE("Usage"), 
    DEMAND("Demand"), 
    PEAK_DEMAND("Peak Demand"), 
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
