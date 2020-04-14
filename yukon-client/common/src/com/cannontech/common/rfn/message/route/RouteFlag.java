package com.cannontech.common.rfn.message.route;

public enum RouteFlag {
    PRIMARY_FORWARD(1),          // Primary forward route
    PRIMARY_REVERSE(2),          // Primary reverse route
    BATTERY(4),                  // Battery Route
    ROUTE_START_GC(8),           // Route start GC
    ROUTE_REMEDIAL_UPDATE(16),   // Route remedial update
    IGNORED(32),                 // Ignored route
    VALID(64),                   // Valid route
    TIMED_OUT(128),              // Route timed out
    ;
    
    private final int routeFlagCodeID;

    private RouteFlag(int neighborFlagCodeID) {
        this.routeFlagCodeID = neighborFlagCodeID;
    }

    public int getRouteFlagCodeID() {
        return routeFlagCodeID;
    }
}