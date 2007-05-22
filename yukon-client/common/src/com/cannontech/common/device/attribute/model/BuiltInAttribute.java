package com.cannontech.common.device.attribute.model;

public enum BuiltInAttribute implements Attribute {
    LOAD_PROFILE, USAGE, DEMAND, PEAK_DEMAND;
    
    public String getDescription() {
        return this.toString(); // until we have something better here...
    }
    
    public String getKey() {
        return this.name();
    }
}
