package com.cannontech.database.db.point.stategroup;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.google.common.collect.Lists;

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
    ARMED(RfnMeterDisconnectState.ARMED)
    ;
    
    private final static List<RfnMeterDisconnectState> otherDisconnectedStates = 
            ListUtils.unmodifiableList(Lists.newArrayList(
                RfnMeterDisconnectState.DISCONNECTED_DEMAND_THRESHOLD_ACTIVE, 
                RfnMeterDisconnectState.CONNECTED_DEMAND_THRESHOLD_ACTIVE, 
                RfnMeterDisconnectState.DISCONNECTED_CYCLING_ACTIVE,
                RfnMeterDisconnectState.CONNECTED_CYCLING_ACTIVE));
    

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
        if(otherDisconnectedStates.contains(nmState)) {
            return DISCONNECTED;
        }
        throw new  IllegalArgumentException();
    }

}
