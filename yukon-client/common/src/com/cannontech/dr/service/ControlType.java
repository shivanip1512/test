package com.cannontech.dr.service;

/**
 * Control types for logging control history.
 * @see ControlHistoryService
 */
public enum ControlType {
    DIGI("Digi Cycle"),
    ECOBEE("Ecobee Cycle"),
    TERMINATE("Control terminate"), //Used for restore
    HONEYWELLWIFI("Honeywell WIFI Cycle"),
    NEST("Nest Cycle"),
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
