package com.cannontech.common.rfn.message.network;

public enum RouteFlagType
{
    PF, // Primary forward route
    PR, // Primary reverse route
    BR, // Battery Route
    GC, // Route start GC
    RU, // Route remedial update
    IR, // Ignored route
    VR, // Valid route
    TO, // Route timed out
}