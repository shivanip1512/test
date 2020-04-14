package com.cannontech.infrastructure.model;

public enum InfrastructureWarningSeverity {
    LOW,
    HIGH,
    CLEAR,
    ;
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
