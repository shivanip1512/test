package com.cannontech.multispeak.data.v5;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspRCDStateKind {

    CONNECT(RCDStateKind.CONNECTED, RfnDisconnectStatusState.CONNECTED, Disconnect410State.CONNECTED, "control connect"),
    DISCONNECT(RCDStateKind.DISCONNECTED, RfnDisconnectStatusState.DISCONNECTED, Disconnect410State.CONFIRMED_DISCONNECTED, "control disconnect"),
    //TODO DISCONNECT_CONFIRMED is a fake enum to support the possible unconfirmed disconnected PLC lookup
//    DISCONNECT_CONFIRMED(LoadActionCode.Disconnect, RFNDisconnectStatusState.DISCONNECTED, Disconnect410State.UNCONFIRMED_DISCONNECTED, "control disconnect"),
//    INITATE_POWER_LIMITATION(LoadActionCode.InitiatePowerLimitation),   //not supported
//    OPEN(LoadActionCode.Open),                                          //not suupported
//    CLOSED(LoadActionCode.Closed),                                      //not supported
    ARMED(RCDStateKind.ARMED, RfnDisconnectStatusState.ARMED, Disconnect410State.CONNECT_ARMED),
    UNKNOWN(RCDStateKind.UNKNOWN, RfnDisconnectStatusState.UNKNOWN, Disconnect410State.UNCONFIRMED_DISCONNECTED), // Old CDEvent code mapped PLC unconfirmedDisconnect to LoadActionCode.Disconnect....
    ;

    private final RCDStateKind rcdStateKind;    
    private final RfnDisconnectStatusState rfnState;
    private final Disconnect410State plcState;
    private final String plcCommandString;
    private final static Logger log = YukonLogManager.getLogger(MspRCDStateKind.class);
    
    private final static ImmutableMap<RfnDisconnectStatusState, MspRCDStateKind> lookupByRfnState;
    private final static ImmutableMap<Disconnect410State, MspRCDStateKind> lookupByPlcState;
    private final static ImmutableMap<RCDStateKind, MspRCDStateKind> lookupByRCDStateKind;

    static {
        try {
            Builder<RfnDisconnectStatusState, MspRCDStateKind> rfnBuilder = ImmutableMap.builder();
            Builder<Disconnect410State, MspRCDStateKind> plcBuilder = ImmutableMap.builder();
            Builder<RCDStateKind, MspRCDStateKind> mspLACBuilder = ImmutableMap.builder();
            for (MspRCDStateKind mspRCDStateKind : values()) {
                rfnBuilder.put(mspRCDStateKind.rfnState, mspRCDStateKind);
                plcBuilder.put(mspRCDStateKind.plcState, mspRCDStateKind);
                mspLACBuilder.put(mspRCDStateKind.rcdStateKind, mspRCDStateKind);
            }
            lookupByRfnState  = rfnBuilder.build();
            lookupByPlcState = plcBuilder.build();
            lookupByRCDStateKind = mspLACBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, possibly have duplicate plc or rfn states.", e);
            throw e;
        }
    }
    
    private MspRCDStateKind(RCDStateKind rcdStateKind,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState) {
        this(rcdStateKind, rfnState, plcState, null);        
    }
    
    private MspRCDStateKind(RCDStateKind rcdStateKind,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState,
            String plcCommandString) {
        this.rcdStateKind = rcdStateKind;
        this.rfnState = rfnState;
        this.plcState = plcState;
        this.plcCommandString = plcCommandString;
    }
    
    public static MspRCDStateKind getForPlcState(Disconnect410State plcState)  {
        return lookupByPlcState.get(plcState);
    }
    
    public static MspRCDStateKind getForRfnState(RfnDisconnectStatusState rfnState) {
        return lookupByRfnState.get(rfnState);
    }
    
    public static MspRCDStateKind getForRCDState(RCDStateKind code) {
        return lookupByRCDStateKind.get(code);
    }
    
    public RCDStateKind getRCDStateKind() {
        return rcdStateKind;
    }
    
    public RfnDisconnectStatusState getRfnState() {
        return rfnState;
    }
    
    public Disconnect410State getPlcState() {
        return plcState;
    }
    
    /**
     * May return a null value if no command string exists for the LoadActionCode.
     * @return
     */
    public String getPlcCommandString() {
        return plcCommandString;
    }
}
