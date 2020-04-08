package com.cannontech.common.rfn.message.neighbor;

public enum NeighborFlag {
    PRIMARY_FORWARD(1),           // PF: Primary forward neighbor
    PRIMARY_REVERSE(2),           // PR: Primary reverse neighbor
    SECONDARY_FOR_ALT_GW(4),      // S2: Secondary neighbor for alternate gateway
    FLOAT(8),                     //  F: Float neighbor
    IGNORED(16),                  // IN: Ignored neighbor
    BATTERY(32),                  // BN: Battery neighbor
    SECONDARY_FOR_SERVING_GW(64), // S1: Secondary neighbor for serving gateway
    ;
    
    private final int neighborFlagCodeID;

    private NeighborFlag(int neighborFlagCodeID) {
        this.neighborFlagCodeID = neighborFlagCodeID;
    }

    public int getNeighborFlagCodeID() {
        return neighborFlagCodeID;
    }
}