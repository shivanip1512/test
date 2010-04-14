package com.cannontech.database.data.point;

public enum ControlType {
    NONE("None"),
    NORMAL("Normal"),
    LATCH("Latch"),
    PSEUDO("Pseudo"),
    SBOLATCH("SBO Latch"),
    SBOPULSE("SBO Pulse");
    
    String displayName;
    
    private ControlType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ControlType getByDisplayName(String name) {
        for (ControlType value : ControlType.values()) {
            if (value.getDisplayName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return NONE;
    }
}
