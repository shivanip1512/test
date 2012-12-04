package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * This enum doesn't represent any field in the database. As of this time, 
 * only one type of control exists for analog points, so an analog point has 
 * an entry in the PointControl table if it has an AnalogControlType of NORMAL,
 * and has no entry in that table otherwise.
 */
public enum AnalogControlType implements DatabaseRepresentationSource {
    NONE("None"),
    NORMAL("Normal");
    
    private final String controlName;
    
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
}
