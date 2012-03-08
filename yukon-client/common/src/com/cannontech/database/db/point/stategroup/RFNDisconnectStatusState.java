package com.cannontech.database.db.point.stategroup;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;

public enum RFNDisconnectStatusState implements PointState {
    UNKNOWN(0, null), 
    CONNECTED(1, RfnMeterDisconnectStatusType.RESUME), 
    DISCONNECTED(2, RfnMeterDisconnectStatusType.TERMINATE), 
    ARMED(3, RfnMeterDisconnectStatusType.ARM),
    ;
    
    private int rawState;
    private RfnMeterDisconnectStatusType type;
    
    private RFNDisconnectStatusState(int rawState, RfnMeterDisconnectStatusType type) {
        this.rawState = rawState;
        this.type = type;
    }

    public static RFNDisconnectStatusState getForType(RfnMeterDisconnectStatusType type) {
        for (RFNDisconnectStatusState state : values()) {
            if (state.type == type) {
                return state;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int getRawState() {
        return rawState;
    }
    
    public RfnMeterDisconnectStatusType getType() {
        return type;
    }
}
