package com.cannontech.multispeak.data.v4;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v4.RCDState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspRCDState {

    OPENED(RCDState.OPENED, RfnDisconnectStatusState.CONNECTED, Disconnect410State.CONNECTED, "control connect"),
    CLOSED(RCDState.CLOSED, RfnDisconnectStatusState.DISCONNECTED, Disconnect410State.CONFIRMED_DISCONNECTED,
            "control disconnect"),
    ARMED(RCDState.ARMED, RfnDisconnectStatusState.ARMED, Disconnect410State.CONNECT_ARMED),
    UNKNOWN(RCDState.UNKNOWN, RfnDisconnectStatusState.UNKNOWN, Disconnect410State.UNCONFIRMED_DISCONNECTED),
    ;

    private final RCDState rcdState;
    private final RfnDisconnectStatusState rfnState;
    private final Disconnect410State plcState;
    private final String plcCommandString;
    private final static Logger log = YukonLogManager.getLogger(MspRCDState.class);

    private final static ImmutableMap<RfnDisconnectStatusState, MspRCDState> lookupByRfnState;
    private final static ImmutableMap<Disconnect410State, MspRCDState> lookupByPlcState;
    private final static ImmutableMap<RCDState, MspRCDState> lookupByRCDStateKind;

    static {
        try {
            Builder<RfnDisconnectStatusState, MspRCDState> rfnBuilder = ImmutableMap.builder();
            Builder<Disconnect410State, MspRCDState> plcBuilder = ImmutableMap.builder();
            Builder<RCDState, MspRCDState> mspLACBuilder = ImmutableMap.builder();
            for (MspRCDState mspRCDState : values()) {
                rfnBuilder.put(mspRCDState.rfnState, mspRCDState);
                plcBuilder.put(mspRCDState.plcState, mspRCDState);
                mspLACBuilder.put(mspRCDState.rcdState, mspRCDState);
            }
            lookupByRfnState = rfnBuilder.build();
            lookupByPlcState = plcBuilder.build();
            lookupByRCDStateKind = mspLACBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, possibly have duplicate plc or rfn states.", e);
            throw e;
        }
    }

    private MspRCDState(RCDState rcdState,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState) {
        this(rcdState, rfnState, plcState, null);
    }

    private MspRCDState(RCDState rcdState,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState,
            String plcCommandString) {
        this.rcdState = rcdState;
        this.rfnState = rfnState;
        this.plcState = plcState;
        this.plcCommandString = plcCommandString;
    }

    public static MspRCDState getForPlcState(Disconnect410State plcState) {
        return lookupByPlcState.get(plcState);
    }

    public static MspRCDState getForRfnState(RfnDisconnectStatusState rfnState) {
        return lookupByRfnState.get(rfnState);
    }

    public static MspRCDState getForRCDState(RCDState code) {
        return lookupByRCDStateKind.get(code);
    }

    public RCDState getRCDState() {
        return rcdState;
    }

    public RfnDisconnectStatusState getRfnState() {
        return rfnState;
    }

    public Disconnect410State getPlcState() {
        return plcState;
    }

    /**
     * May return a null value if no command string exists.
     * 
     * @return
     */
    public String getPlcCommandString() {
        return plcCommandString;
    }
}