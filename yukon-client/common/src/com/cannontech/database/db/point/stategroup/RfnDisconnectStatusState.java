package com.cannontech.database.db.point.stategroup;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;

/**
 * This is a little unfortunate, but because we are unable to change anything within the network manager "shared"
 *  message files without affecting network manager code, all this enum does is create a 
 *  wrapper around the network manager (serialized) enum.
 *  A getter on RfnMeterDisconnectStatusType.type would have been helpful, too....but instead we are duplicating 
 *  some associations between state and type.
 */
public enum RfnDisconnectStatusState implements PointState {
    UNKNOWN(RfnMeterDisconnectState.UNKNOWN),
    CONNECTED(RfnMeterDisconnectState.CONNECTED),
    DISCONNECTED(RfnMeterDisconnectState.DISCONNECTED),
    ARMED(RfnMeterDisconnectState.ARMED), ;

    private final RfnMeterDisconnectState nmReferenceState;

    private RfnDisconnectStatusState(RfnMeterDisconnectState nmReferenceState) {
        this.nmReferenceState = nmReferenceState;
    }

    @Override
    public int getRawState() {
        return nmReferenceState.getRawState();
    }

    public RfnMeterDisconnectState getNmReferenceState() {
        return nmReferenceState;
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
