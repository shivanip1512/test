package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectState {

    UNKNOWN(0, null), 
    CONNECTED(1, RfnMeterDisconnectStatusType.RESUME), 
    DISCONNECTED(2, RfnMeterDisconnectStatusType.TERMINATE), 
    ARMED(3, RfnMeterDisconnectStatusType.ARM);
    
    private int rawState;
    private RfnMeterDisconnectStatusType type; 
    
    private RfnMeterDisconnectState(int rawState, RfnMeterDisconnectStatusType type) {
        this.rawState = rawState;
        this.type = type;
    }
    
    public static RfnMeterDisconnectState getForType(RfnMeterDisconnectStatusType type) {
        for (RfnMeterDisconnectState state : values()) {
            if (state.type == type) {
                return state;
            }
        }
        throw new IllegalArgumentException();
    }
    
    public int getRawState() {
        return rawState;
    }
}