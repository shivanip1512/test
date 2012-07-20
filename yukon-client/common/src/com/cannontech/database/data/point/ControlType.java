package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlType implements DatabaseRepresentationSource {
    NONE("None", false),
    NORMAL("Normal", true),
    LATCH("Latch", true),
    PSEUDO("Pseudo", false),
    SBOLATCH("SBO Latch", true),
    SBOPULSE("SBO Pulse", true);
    
    String controlName;
    boolean hasSettings;
    
    private ControlType(String controlName, boolean hasSettings) {
        this.controlName = controlName;
        this.hasSettings = hasSettings;
    }
    
    public boolean hasSettings() {
        return hasSettings;
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
