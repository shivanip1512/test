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
    
    public static Pair<JSONObject, JSONObject> createTCPPortRequiredFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .withEnable(Optional.empty())
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> createLocalSharedPortAllFields() {        
        return new LocalSharedPortBuilder(Optional.empty(), CommChannelType.LOCAL_SHARED)
        .withPhysicalPort(Optional.empty())
        .withCarrierDetectWaitMs(Optional.empty())
        .withProtocolWrap(Optional.empty())
        .withSharedPortType(Optional.empty())
        .withSocketNumber(Optional.empty())
        .withBaudRate(Optional.empty())
        .withEnable(Optional.empty())
        .withExtraTimeOut(Optional.empty())
        .withPostTxWait(Optional.empty())
        .withPreTxWait(Optional.empty())
        .withReceiveDataWait(Optional.empty())
        .withRtsToTxWait(Optional.empty())
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