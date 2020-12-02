package com.eaton.builders.assets.commchannel;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.assets.commchannel.CommChannelLocalSharedPortCreateBuilder.LocalSharedPortBuilder;
import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;

public class CommChannelCreateService {

    public static Pair<JSONObject, JSONObject> createTCPPortAllFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .withEnable(Optional.empty())
                .withExtraTimeOut(Optional.empty())
                .withPostTxWait(Optional.empty())
                .withPreTxWait(Optional.empty())
                .withReceiveDataWait(Optional.empty())
                .withRtsToTxWait(Optional.empty())
                .create();
    }
    
    public static JSONObject buildTCPPortAllFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .withEnable(Optional.empty())
                .withExtraTimeOut(Optional.empty())
                .withPostTxWait(Optional.empty())
                .withPreTxWait(Optional.empty())
                .withReceiveDataWait(Optional.empty())
                .withRtsToTxWait(Optional.empty())
                .build();
    }
    
    public static Pair<JSONObject, JSONObject> createTCPPortRequiredFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .withEnable(Optional.empty())
                .create();
    }
    
    public static JSONObject buildTCPPortRequiredFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .withEnable(Optional.empty())
                .build();
    }
    
    public static Pair<JSONObject, JSONObject> createLocalSharedPortAllFields() {        
        return new LocalSharedPortBuilder(Optional.empty(), CommChannelType.LOCAL_SHARED)
        .withPhysicalPort(Optional.of(CommChannelTypes.PhysicalPortType.OTHER))
        .withCarrierDetectWaitMs(Optional.of(638))
        .withProtocolWrap(Optional.of(CommChannelTypes.ProtocolWrapType.NONE))
        .withSharedPortType(Optional.of(CommChannelTypes.SharedPortType.ACS))
        .withSocketNumber(Optional.of(51397))
        .withBaudRate(Optional.of(CommChannelTypes.BaudRateType.BAUD_14400))
        .withEnable(Optional.of(true))
        .withExtraTimeOut(Optional.of(269))
        .withPostTxWait(Optional.of(7879161))
        .withPreTxWait(Optional.of(8815381))
        .withReceiveDataWait(Optional.of(808))
        .withRtsToTxWait(Optional.of(3181746))
        .create();
    }
    
    public static Pair<JSONObject, JSONObject> createLocalSharedPortRequiredFields() {        
        return new LocalSharedPortBuilder(Optional.empty(), CommChannelType.LOCAL_SHARED)
        .withPhysicalPort(Optional.empty())
        .withBaudRate(Optional.empty())
        .withEnable(Optional.empty())
        .create();
    }
    
    public static Pair<JSONObject, JSONObject> createTerminalServerAllFields() {
        return new CommChannelTerminalServerCreateBuilder.Builder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> createTerminalServerRequiredFields() {
        return new CommChannelTerminalServerCreateBuilder.Builder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> createUdpTerminalServerAllFields() {
        return new CommChannelUdpTerminalServerCreateBuilder.Builder(Optional.empty(), CommChannelType.UDPPORT)
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> createUdpTerminalServerRequiredFields() {
        return new CommChannelUdpTerminalServerCreateBuilder.Builder(Optional.empty(), CommChannelType.UDPPORT)
                .create();
    }
}