package com.cannontech.common.config;

public enum RfnMeterDisconnectArming {
    
    ARM("TRUE"),
    CONNECT("FALSE"),
    BOTH("BOTH");
    
    private String cparmValue;
    
    private RfnMeterDisconnectArming(String cparmValue) {
        this.cparmValue = cparmValue;
    }
    
    public static RfnMeterDisconnectArming getForCparm(String cparmValue) {
        for (RfnMeterDisconnectArming entry : values()) {
            if (entry.cparmValue.equalsIgnoreCase(cparmValue)) {
                return entry;
            }
        }
        throw new IllegalArgumentException();
    }
}