package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectState {

    UNKNOWN(0, null), 
    CONNECTED(1, RfnMeterDisconnectStatusType.RESUME), 
    DISCONNECTED(2, RfnMeterDisconnectStatusType.TERMINATE), 
    ARMED(3, RfnMeterDisconnectStatusType.ARM),
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE(4, RfnMeterDisconnectStatusType.TERMINATE),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_DEMAND_THRESHOLD_ACTIVE(5, RfnMeterDisconnectStatusType.TERMINATE),
    DISCONNECTED_CYCLING_ACTIVE(6, RfnMeterDisconnectStatusType.TERMINATE),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_CYCLING_ACTIVE(7, RfnMeterDisconnectStatusType.TERMINATE)
    ;
    
    
    private final int rawState;
    private final RfnMeterDisconnectStatusType type; 
    
    private RfnMeterDisconnectState(int rawState, RfnMeterDisconnectStatusType type) {
        this.rawState = rawState;
        this.type = type;
    }
    
    public int getRawState() {
        return rawState;
    }
}