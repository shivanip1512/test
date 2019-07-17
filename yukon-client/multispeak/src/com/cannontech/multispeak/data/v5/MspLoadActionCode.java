package com.cannontech.multispeak.data.v5;

import org.apache.logging.log4j.Logger;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.RfnMeterDisconnectArming;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v5.enumerations.LoadActionCodeKind;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspLoadActionCode {

    CONNECT(LoadActionCodeKind.CONNECT, RfnDisconnectStatusState.CONNECTED, Disconnect410State.CONNECTED),
    DISCONNECT(LoadActionCodeKind.DISCONNECT, RfnDisconnectStatusState.DISCONNECTED, Disconnect410State.CONFIRMED_DISCONNECTED),
    //TODO DISCONNECT_CONFIRMED is a fake enum to support the possible unconfirmed disconnected PLC lookup
//    DISCONNECT_CONFIRMED(LoadActionCode.Disconnect, RFNDisconnectStatusState.DISCONNECTED, Disconnect410State.UNCONFIRMED_DISCONNECTED, "control disconnect"),
//    INITATE_POWER_LIMITATION(LoadActionCode.InitiatePowerLimitation),   //not supported
//    OPEN(LoadActionCode.Open),                                          //not suupported
//    CLOSED(LoadActionCode.Closed),                                      //not supported
    ARM(LoadActionCodeKind.ARM, RfnDisconnectStatusState.ARMED, Disconnect410State.CONNECT_ARMED),
    UNKNOWN(LoadActionCodeKind.UNKNOWN, RfnDisconnectStatusState.UNKNOWN, Disconnect410State.UNCONFIRMED_DISCONNECTED), // Old CDEvent code mapped PLC unconfirmedDisconnect to LoadActionCode.Disconnect....
    ;

    private final LoadActionCodeKind loadActionCode;    
    private final RfnDisconnectStatusState rfnState;
    private final Disconnect410State plcState;
    private final static Logger log = YukonLogManager.getLogger(MspLoadActionCode.class);
    
    private final static ImmutableMap<RfnDisconnectStatusState, MspLoadActionCode> lookupByRfnState;
    private final static ImmutableMap<Disconnect410State, MspLoadActionCode> lookupByPlcState;
    private final static ImmutableMap<LoadActionCodeKind, MspLoadActionCode> lookupByLoadActionCode;

    static {
        try {
            Builder<RfnDisconnectStatusState, MspLoadActionCode> rfnBuilder = ImmutableMap.builder();
            Builder<Disconnect410State, MspLoadActionCode> plcBuilder = ImmutableMap.builder();
            Builder<LoadActionCodeKind, MspLoadActionCode> mspLACBuilder = ImmutableMap.builder();
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

    private MspLoadActionCode(LoadActionCodeKind loadActionCode, RfnDisconnectStatusState rfnState,
            Disconnect410State plcState) {
        this.loadActionCode = loadActionCode;
        this.rfnState = rfnState;
        this.plcState = plcState;
    }

    public static MspLoadActionCode getForPlcState(Disconnect410State plcState)  {
        return lookupByPlcState.get(plcState);
    }
    
    public static MspLoadActionCode getForRfnState(RfnDisconnectStatusState rfnState) {
        return lookupByRfnState.get(rfnState);
    }
    
    public static MspLoadActionCode getForLoadActionCode(LoadActionCodeKind code) {
        return lookupByLoadActionCode.get(code);
    }
    
    public LoadActionCodeKind getLoadActionCode() {
        return loadActionCode;
    }
    
    public RfnDisconnectStatusState getRfnState() {
        return rfnState;
    }
    
    public Disconnect410State getPlcState() {
        return plcState;
    }

    /**
     * Returns a RfnMeterDisconnectStatusType ("command") representative of the load action code requested.
     * Requires a configSource to determine if ARM is being supported or not.
     * 
     * @param configSource
     * @return
     */
    public RfnMeterDisconnectCmdType getRfnMeterDisconnectStatusType(ConfigurationSource configSource) {
        switch (this) {
        case CONNECT:
            return RfnMeterDisconnectCmdType.RESUME;
        case ARM: {
            String arm = configSource.getString(MasterConfigString.RFN_METER_DISCONNECT_ARMING, "FALSE");
            RfnMeterDisconnectArming mode = RfnMeterDisconnectArming.getForCparm(arm);
            switch (mode) {
            case ARM:
            case BOTH:
                return RfnMeterDisconnectCmdType.ARM;
            default:
                return RfnMeterDisconnectCmdType.RESUME;
            }
        }
        case DISCONNECT:
            return RfnMeterDisconnectCmdType.TERMINATE;
        case UNKNOWN:
        default:
            return null;
        }
    }

    /**
     * May return a null value if no command string exists for the LoadActionCode.
     */
    public String getPlcCommandString() {
        switch (this) {
        case CONNECT:
        case ARM:
            return DisconnectCommand.CONNECT.getPlcCommand();
        case DISCONNECT:
            return DisconnectCommand.DISCONNECT.getPlcCommand();
        case UNKNOWN:
        default:
            return null;
        }
    }

}
