package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlType implements DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal"),
    LATCH("Latch"),
    PSEUDO("Pseudo"),
    SBOLATCH("SBO Latch"),
    SBOPULSE("SBO Pulse");
    
    String controlName;
    
    private ControlType(String controlName) {
        this.controlName = controlName;
    }
    
    public String getControlName() {
        return controlName;
    }
    
    public Object getDatabaseRepresentation() {
        return getControlName();
    }
    
    public ControlType getByDisplayName(String name) {
        for (ControlType value : ControlType.values()) {
            if (value.getControlName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return NONE;
    }
}
