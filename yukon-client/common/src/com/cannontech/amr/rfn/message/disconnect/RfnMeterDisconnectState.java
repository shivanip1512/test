package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectState {

    UNKNOWN(0, null), 
    CONNECTED(1, RfnMeterDisconnectCmdType.RESUME), 
    DISCONNECTED(2, RfnMeterDisconnectCmdType.TERMINATE), 
    ARMED(3, RfnMeterDisconnectCmdType.ARM),
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE(4, RfnMeterDisconnectCmdType.TERMINATE),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_DEMAND_THRESHOLD_ACTIVE(5, RfnMeterDisconnectCmdType.TERMINATE),
    DISCONNECTED_CYCLING_ACTIVE(6, RfnMeterDisconnectCmdType.TERMINATE),
    /**
     * Meter is in disconnected "state" based on Mode, but is technically connected until
     * configuration requirements for disconnect mode are met
     */
    CONNECTED_CYCLING_ACTIVE(7, RfnMeterDisconnectCmdType.TERMINATE)
    ;
    
    
    private final int rawState;
    private final RfnMeterDisconnectCmdType type; 
    
    private RfnMeterDisconnectState(int rawState, RfnMeterDisconnectCmdType type) {
        this.rawState = rawState;
        this.type = type;
    }
    
    /**
     * The raw state is not the actual value reported from the RF device. It is for Yukon's point 
     * data only.
     */
    public int getRawState() {
        return rawState;
    }

    public RfnMeterDisconnectCmdType getType() {
        return type;
    }
}