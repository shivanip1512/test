package com.cannontech.common.rfn.message.neighbor;

public enum NeighborFlag {
    NEIGHBOR_FLAG_PRIMARY_FORWARD(1),           // PF: Primary forward neighbor
    NEIGHBOR_FLAG_PRIMARY_REVERSE(2),           // PR: Primary reverse neighbor
    NEIGHBOR_FLAG_SECONDARY_FOR_ALT_GW(4),      // S2: Secondary neighbor for alternate gateway
    NEIGHBOR_FLAG_FLOAT(8),                     //  F: Float neighbor
    NEIGHBOR_FLAG_IGNORED(16),                  // IN: Ignored neighbor
    NEIGHBOR_FLAG_BATTERY(32),                  // BN: Battery neighbor
    NEIGHBOR_FLAG_SECONDARY_FOR_SERVING_GW(64), // S1: Secondary neighbor for serving gateway
    ;
    
    private final int neighborFlagCodeID;

    private NeighborFlag(int neighborFlagCodeID) {
        this.neighborFlagCodeID = neighborFlagCodeID;
    }

    public int getNeighborFlagCodeID() {
        return neighborFlagCodeID;
    }
}