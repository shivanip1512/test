package com.cannontech.amr.deviceDataMonitor.model;

public enum ProcessorType {
    STATE,
    RANGE,
    LESS,
    GREATER;
    
    public boolean isStateBased() {
        return this == STATE;
    }
    
    public boolean isValueBased() {
        return this != STATE;
    }
}
