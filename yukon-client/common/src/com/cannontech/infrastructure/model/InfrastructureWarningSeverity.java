package com.cannontech.infrastructure.model;

import org.apache.commons.lang3.StringUtils;

public enum InfrastructureWarningSeverity {
    LOW,
    HIGH,
    CLEAR,
    ;
    
    @Override
    public String toString() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
