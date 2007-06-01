package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE, USAGE, DEMAND, PEAK_DEMAND, BLINK_COUNT, 
    DISCONNECT_STATUS, VOLTAGE, VOLTAGE_PROFILE;
    
    public String getDescription() {
        return this.toString(); // until we have something better here...
    }
    
    public String getKey() {
        return this.name();
    }
}
