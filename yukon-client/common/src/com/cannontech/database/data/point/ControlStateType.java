package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ControlStateType implements DatabaseRepresentationSource {
    NONE("none"),
    OPEN("control open"),
    CLOSE("control close"),
    PULSE("control pulse"),
    DISABLE_OVUV_702X("putvalue analog 1 0"), // used for auto volt control point
    ENABLE_OVUV_702X("putvalue analog 1 1"),  // ditto
    ;
    
    private final String controlCommand;
    
    private ControlStateType(String controlCommand) {
        this.controlCommand = controlCommand;
    }
    
    public String getControlCommand() {
        return controlCommand;
    }
    
    public Object getDatabaseRepresentation() {
        return getControlCommand();
    }
}
