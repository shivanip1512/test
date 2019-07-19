package com.cannontech.database.db.point.stategroup;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableMap;
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
    ARMED(RfnMeterDisconnectState.ARMED),
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE(RfnMeterDisconnectState.DISCONNECTED_DEMAND_THRESHOLD_ACTIVE),
    CONNECTED_DEMAND_THRESHOLD_ACTIVE(RfnMeterDisconnectState.CONNECTED_DEMAND_THRESHOLD_ACTIVE),
    DISCONNECTED_CYCLING_ACTIVE(RfnMeterDisconnectState.DISCONNECTED_CYCLING_ACTIVE),
    CONNECTED_CYCLING_ACTIVE(RfnMeterDisconnectState.CONNECTED_CYCLING_ACTIVE)
    ;
      
    private final RfnMeterDisconnectState nmReferenceState;
    private final static Logger log = YukonLogManager.getLogger(RfnDisconnectStatusState.class);
    private final static ImmutableMap<RfnMeterDisconnectState, RfnDisconnectStatusState> lookupByNmRef;
    
    static {
        try {
            ImmutableMap.Builder<RfnMeterDisconnectState, RfnDisconnectStatusState> nmRefBuilder = ImmutableMap.builder();
            for (RfnDisconnectStatusState rfnDisconnect : values()) {
                nmRefBuilder.put(rfnDisconnect.nmReferenceState, rfnDisconnect);
            }
            lookupByNmRef = nmRefBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate abbreviation.", e);
            throw e;
        }
    }

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
        return lookupByNmRef.get(nmState);
    }
}
