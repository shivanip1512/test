package com.cannontech.multispeak.data;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspLoadActionCode {

    CONNECT(LoadActionCode.Connect, RfnDisconnectStatusState.CONNECTED, Disconnect410State.CONNECTED, "control connect"),
    DISCONNECT(LoadActionCode.Disconnect, RfnDisconnectStatusState.DISCONNECTED, Disconnect410State.CONFIRMED_DISCONNECTED, "control disconnect"),
    //TODO DISCONNECT_CONFIRMED is a fake enum to support the possible unconfirmed disconnected PLC lookup
//    DISCONNECT_CONFIRMED(LoadActionCode.Disconnect, RFNDisconnectStatusState.DISCONNECTED, Disconnect410State.UNCONFIRMED_DISCONNECTED, "control disconnect"),
//    INITATE_POWER_LIMITATION(LoadActionCode.InitiatePowerLimitation),   //not supported
//    OPEN(LoadActionCode.Open),                                          //not suupported
//    CLOSED(LoadActionCode.Closed),                                      //not supported
    ARMED(LoadActionCode.Armed, RfnDisconnectStatusState.ARMED, Disconnect410State.CONNECT_ARMED),
    UNKNOWN(LoadActionCode.Unknown, RfnDisconnectStatusState.UNKNOWN, Disconnect410State.UNCONFIRMED_DISCONNECTED), // Old CDEvent code mapped PLC unconfirmedDisconnect to LoadActionCode.Disconnect....
    ;

    private final LoadActionCode loadActionCode;    
    private final RfnDisconnectStatusState rfnState;
    private final Disconnect410State plcState;
    private final String plcCommandString;
    private final static Logger log = YukonLogManager.getLogger(MspLoadActionCode.class);
    
    private final static ImmutableMap<RfnDisconnectStatusState, MspLoadActionCode> lookupByRfnState;
    private final static ImmutableMap<Disconnect410State, MspLoadActionCode> lookupByPlcState;
    private final static ImmutableMap<LoadActionCode, MspLoadActionCode> lookupByLoadActionCode;

    static {
        try {
            Builder<RfnDisconnectStatusState, MspLoadActionCode> rfnBuilder = ImmutableMap.builder();
            Builder<Disconnect410State, MspLoadActionCode> plcBuilder = ImmutableMap.builder();
            Builder<LoadActionCode, MspLoadActionCode> mspLACBuilder = ImmutableMap.builder();
            for (MspLoadActionCode mspLoadActionCode : values()) {
                rfnBuilder.put(mspLoadActionCode.rfnState, mspLoadActionCode);
                plcBuilder.put(mspLoadActionCode.plcState, mspLoadActionCode);
                mspLACBuilder.put(mspLoadActionCode.loadActionCode, mspLoadActionCode);
            }
            lookupByRfnState  = rfnBuilder.build();
            lookupByPlcState = plcBuilder.build();
            lookupByLoadActionCode = mspLACBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, possibly have duplicate plc or rfn states.", e);
            throw e;
        }
    }
    
    private MspLoadActionCode(LoadActionCode loadActionCode,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState) {
        this(loadActionCode, rfnState, plcState, null);        
    }
    
    private MspLoadActionCode(LoadActionCode loadActionCode,
            RfnDisconnectStatusState rfnState, Disconnect410State plcState,
            String plcCommandString) {
        this.loadActionCode = loadActionCode;
        this.rfnState = rfnState;
        this.plcState = plcState;
        this.plcCommandString = plcCommandString;
    }
    
    public static MspLoadActionCode getForPlcState(Disconnect410State plcState)  {
        return lookupByPlcState.get(plcState);
    }
    
    public static MspLoadActionCode getForRfnState(RfnDisconnectStatusState rfnState) {
        return lookupByRfnState.get(rfnState);
    }
    
    public static MspLoadActionCode getForLoadActionCode(LoadActionCode code) {
        return lookupByLoadActionCode.get(code);
    }
    
    public LoadActionCode getLoadActionCode() {
        return loadActionCode;
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
