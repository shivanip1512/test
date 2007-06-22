package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE("Load Profile"), USAGE("Usage"), DEMAND("Demand"), PEAK_DEMAND("Peak Demand"), 
    BLINK_COUNT("Blink Count"), DISCONNECT_STATUS("Disconnect Status"), VOLTAGE("Voltage"), 
    VOLTAGE_PROFILE("Voltage Profile");
    
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
