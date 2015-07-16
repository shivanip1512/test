package com.cannontech.database.data.point;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * This enum represents the ControlType field of the PointStatusControl table.
 */
public enum StatusControlType implements DisplayableEnum, DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal"),
    LATCH("Latch"),
    PSEUDO("Pseudo"),
    SBOLATCH("SBO Latch"),
    SBOPULSE("SBO Pulse");
    
    private static final String baseKey = "yukon.common.point.statusControlType.";
    
    private final String controlName;
    
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
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
