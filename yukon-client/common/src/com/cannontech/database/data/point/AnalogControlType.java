package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum AnalogControlType implements DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal");
    
    String controlName;
    
    private AnalogControlType(String controlName) {
        this.controlName = controlName;
    }
    
    public String getControlName() {
        return controlName;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getControlName();
    }
    
    public AnalogControlType getByDisplayName(String name) {
        for (AnalogControlType value : AnalogControlType.values()) {
            if (value.getControlName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return NONE;
    }
}
