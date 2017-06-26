package com.cannontech.infrastructure.model;

public enum InfrastructureWarningSeverity {
    LOW,
    HIGH,
    ;
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
