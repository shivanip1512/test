package com.cannontech.dr.service;

/**
 * Control types for logging control history.
 * @see ControlHistoryService
 */
public enum ControlType {
    DIGI("Digi Control"),
    ECOBEE("Ecobee Control"),
    TERMINATE("Control terminate"), //Used for restore
    ;
    
    private String stringValue;
    
    private ControlType(String stringValue) {
        this.stringValue = stringValue;
    }
    
    @Override
    public String toString() {
        return stringValue;
    }
}
