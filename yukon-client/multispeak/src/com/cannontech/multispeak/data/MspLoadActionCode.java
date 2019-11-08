package com.cannontech.multispeak.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.RfnMeterDisconnectArming;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspLoadActionCode {

    CONNECT(LoadActionCode.CONNECT, Disconnect410State.CONNECTED, RfnDisconnectStatusState.CONNECTED),
    DISCONNECT(LoadActionCode.DISCONNECT, Disconnect410State.CONFIRMED_DISCONNECTED, 
               RfnDisconnectStatusState.DISCONNECTED, RfnDisconnectStatusState.DISCONNECTED_DEMAND_THRESHOLD_ACTIVE, RfnDisconnectStatusState.CONNECTED_DEMAND_THRESHOLD_ACTIVE, 
               RfnDisconnectStatusState.DISCONNECTED_CYCLING_ACTIVE, RfnDisconnectStatusState.CONNECTED_CYCLING_ACTIVE),
    //TODO DISCONNECT_CONFIRMED is a fake enum to support the possible unconfirmed disconnected PLC lookup
//    DISCONNECT_CONFIRMED(LoadActionCode.Disconnect, RFNDisconnectStatusState.DISCONNECTED, Disconnect410State.UNCONFIRMED_DISCONNECTED, "control disconnect"),
//    INITATE_POWER_LIMITATION(LoadActionCode.InitiatePowerLimitation),   //not supported
//    OPEN(LoadActionCode.Open),                                          //not suupported
//    CLOSED(LoadActionCode.Closed),                                      //not supported
    ARM(LoadActionCode.ARM, null, null), // no yukon states will ever return this LAC
    ARMED(LoadActionCode.ARMED, Disconnect410State.CONNECT_ARMED, RfnDisconnectStatusState.ARMED),
    UNKNOWN(LoadActionCode.UNKNOWN, Disconnect410State.UNCONFIRMED_DISCONNECTED, RfnDisconnectStatusState.UNKNOWN), // Old CDEvent code mapped PLC unconfirmedDisconnect to LoadActionCode.Disconnect....
    ;

    private final LoadActionCode loadActionCode;
    private final Disconnect410State plcState;
    private final List<RfnDisconnectStatusState> rfnStates;
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
                if (!mspLoadActionCode.rfnStates.isEmpty()) {
                    for (RfnDisconnectStatusState rfnDisconnectStatusState : mspLoadActionCode.rfnStates) {
                        rfnBuilder.put(rfnDisconnectStatusState, mspLoadActionCode);
                    }
                }
                if (mspLoadActionCode.plcState != null) {
                    plcBuilder.put(mspLoadActionCode.plcState, mspLoadActionCode);
                }
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
    
    private MspLoadActionCode(LoadActionCode loadActionCode, Disconnect410State plcState,
            RfnDisconnectStatusState... rfnStates) {
        this.loadActionCode = loadActionCode;
        this.plcState = plcState;
        if (rfnStates != null) {
            this.rfnStates = Arrays.asList(rfnStates);
        } else {
            this.rfnStates = Collections.emptyList();
        }
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
    
    public Disconnect410State getPlcState() {
        return plcState;
    }

    /**
     * Returns a RfnMeterDisconnectCmdType ("command") representative of the load action code requested.
     * Requires a configSource to determine if ARM is being supported or not.
     * 
     * @param configSource
     * @return
     */
    public RfnMeterDisconnectCmdType getRfnMeterDisconnectCmdType(ConfigurationSource configSource) {
        switch (this) {
        case CONNECT:
            return RfnMeterDisconnectCmdType.RESUME;
        case ARMED:
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
        case ARMED:
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
