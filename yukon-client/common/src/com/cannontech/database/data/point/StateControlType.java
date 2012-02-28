package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StateControlType implements DatabaseRepresentationSource {
    NONE("none"),
    OPEN("control open"),
    CLOSE("control close"),
    ;
    
    private final String controlCommand;
    
    private StateControlType(String controlCommand) {
        this.controlCommand = controlCommand;
    }
    
    public String getControlCommand() {
        return controlCommand;
    }
    
    public Object getDatabaseRepresentation() {
        return getControlCommand();
    }
}
