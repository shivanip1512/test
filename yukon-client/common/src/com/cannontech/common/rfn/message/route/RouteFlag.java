package com.cannontech.common.rfn.message.route;

public enum RouteFlag {
    ROUTE_FLAG_PRIMARY_FORWARD(1),          // Primary forward route
    ROUTE_FLAG_PRIMARY_REVERSE(2),          // Primary reverse route
    ROUTE_FLAG_BATTERY(4),                  // Battery Route
    ROUTE_FLAG_ROUTE_START_GC(8),           // Route start GC
    ROUTE_FLAG_ROUTE_REMEDIAL_UPDATE(16),   // Route remedial update
    ROUTE_FLAG_IGNORED(32),                 // Ignored route
    ROUTE_FLAG_VALID(64),                   // Valid route
    ROUTE_FLAG_TIMED_OUT(128),              // Route timed out
    ;
    
    private final int routeFlagCodeID;

    private RouteFlag(int neighborFlagCodeID) {
        this.routeFlagCodeID = neighborFlagCodeID;
    }

    public int getRouteFlagCodeID() {
        return routeFlagCodeID;
    }
}