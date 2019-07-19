package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectState {

    UNKNOWN(0), 
    CONNECTED(1), 
    DISCONNECTED(2), 
    ARMED(3),
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE(4),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_DEMAND_THRESHOLD_ACTIVE(5),
    DISCONNECTED_CYCLING_ACTIVE(6),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_CYCLING_ACTIVE(7)
    ;
    
    
    private final int rawState;
    
    private RfnMeterDisconnectState(int rawState) {
        this.rawState = rawState;
    }
    
    /**
     * The raw state is not the actual value reported from the RF device. It is for Yukon's point 
     * data only.
     */
    public int getRawState() {
        return rawState;
    }
}