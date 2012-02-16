package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StateControlType implements DatabaseRepresentationSource {
    NONE("none"),
    OPEN("control open"),
    CLOSE("control close"),
    ;
    
    String controlName;
    
    private StateControlType(String controlName) {
        this.controlName = controlName;
    }
    
    public String getControlName() {
        return controlName;
    }
    
    public Object getDatabaseRepresentation() {
        return getControlName();
    }
}
