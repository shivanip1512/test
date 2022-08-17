package com.cannontech.common.rfn.message.network;

public enum NeighborFlagType
{
    PF, // Primary forward neighbor
    PR, // Primary reverse neighbor
    S2, // Secondary neighbor for alternate gateway
    F,  // Float neighbor
    IN, // Ignored neighbor
    BN, // Battery neighbor
    S1, // Secondary neighbor for serving gateway
}