package com.cannontech.database.db.point.stategroup;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;

/**
 * This is a little unfortunate, but because we are unable to change anything within the network manager "shared"
 *  message files without affecting network manager code, all this enum does is create a 
 *  wrapper around the network manager (serialized) enum.
 *  A getter on RfnMeterDisconnectStatusType.type would have been helpful, too....but instead we are duplicating 
 *  some associations between state and type.
 */
public enum RfnDisconnectStatusState implements PointState {
    UNKNOWN(RfnMeterDisconnectState.UNKNOWN, null), 
    CONNECTED(RfnMeterDisconnectState.CONNECTED, RfnMeterDisconnectStatusType.RESUME), 
    DISCONNECTED(RfnMeterDisconnectState.DISCONNECTED, RfnMeterDisconnectStatusType.TERMINATE), 
    ARMED(RfnMeterDisconnectState.ARMED, RfnMeterDisconnectStatusType.ARM),
    ;

    private final RfnMeterDisconnectState nmReferenceState;
    private final RfnMeterDisconnectStatusType type;
    
    private RfnDisconnectStatusState(RfnMeterDisconnectState nmReferenceState, RfnMeterDisconnectStatusType type) {
        this.nmReferenceState = nmReferenceState;
        this.type = type;
    }

    @Override
    public int getRawState() {
        return nmReferenceState.getRawState();
    }
    
    public RfnMeterDisconnectState getNmReferenceState() {
        return nmReferenceState;
    }
    
    public RfnMeterDisconnectStatusType getType() {
        return type;
    }
    
    /**
     * Returns the RfnDisconnectStatusState for the Network Manager serialized 
     *  message state (RfnMeterDisconnectState). 
     */
    public static RfnDisconnectStatusState getForNmState(RfnMeterDisconnectState nmState) {
        for (RfnDisconnectStatusState state : values()) {
            if (state.nmReferenceState == nmState) {
                return state;
            }
        }
        throw new  IllegalArgumentException();
    }
    
    
}
