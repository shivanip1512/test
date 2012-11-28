package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StatusControlType implements DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal"),
    LATCH("Latch"),
    PSEUDO("Pseudo"),
    SBOLATCH("SBO Latch"),
    SBOPULSE("SBO Pulse");
    
    String controlName;
    
    private StatusControlType(String controlName) {
        this.controlName = controlName;
    }
    
    public String getControlName() {
        return controlName;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getControlName();
    }
    
    public StatusControlType getByDisplayName(String name) {
        for (StatusControlType value : StatusControlType.values()) {
            if (value.getControlName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return NONE;
    }
}
