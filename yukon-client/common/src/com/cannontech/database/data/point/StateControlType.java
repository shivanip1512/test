package com.cannontech.database.data.point;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StateControlType implements DatabaseRepresentationSource {
    NONE("none"),
    OPEN("control open"),
    CLOSE("control close"),
    DISABLE_OVUV_702X("putvalue analog 1 0"), // used for auto volt control point
    ENABLE_OVUV_702X("putvalue analog 1 1"),  // ditto
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
