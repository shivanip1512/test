package com.cannontech.amr.rfn.message.status.type;

public enum RfnMeterDisconnectMeterMode {
    TERMINATE,                      // Legacy or on-demand disconnect mode
    ARM,                            // Legacy or on-demand disconnect mode
    RESUME,                         // Legacy or on-demand disconnect mode
    ON_DEMAND_CONFIGURATION,        // On-demand disconnect mode
    DEMAND_THRESHOLD_CONFIGURATION, // Demand threshold disconnect mode
    DEMAND_THRESHOLD_ACTIVATE,      // Demand threshold disconnect mode
    DEMAND_THRESHOLD_DEACTIVATE,    // Demand threshold disconnect mode
    CYCLING_CONFIGURATION,          // Cycling disconnect mode
    CYCLING_ACTIVATE,               // Cycling disconnect mode
    CYCLING_DEACTIVATE,             // Cycling disconnect mode
}
