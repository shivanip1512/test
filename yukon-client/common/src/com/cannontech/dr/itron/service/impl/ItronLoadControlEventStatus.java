package com.cannontech.dr.itron.service.impl;

import java.util.Optional;

public enum ItronLoadControlEventStatus {
    EVENT_RECEIVED("event received."),
    EVENT_STARTED("event started."),
    EVENT_CANCELLED("event cancelled."),
    EVENT_COMPLETED("event completed."),
    //Events that are not used yet:
    //  EVENT_REJECTED("event rejected."),
    //  EVENT_STATE_UNKNOWN("event state unknown"),
    ;
    
    private final String dataValue;
    
    private ItronLoadControlEventStatus(String dataValue) {
        this.dataValue = dataValue;
    }
    
    public static Optional<ItronLoadControlEventStatus> fromValue(String value) {
        for (ItronLoadControlEventStatus status : values()) {
            if (value.trim().equalsIgnoreCase(status.dataValue)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
