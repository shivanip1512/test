package com.eaton.builders.assets.commchannel;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.assets.commchannel.CommChannelLocalSharedPortCreateBuilder.LocalSharedPortBuilder;
import com.eaton.builders.assets.commchannel.CommChannelTerminalServerCreateBuilder.TerminalServerBuilder;
import com.eaton.builders.assets.commchannel.CommChannelUdpTerminalServerCreateBuilder.UdpTerminalServerBuilder;
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
                .create();
    }
    
    public static JSONObject buildTCPPortRequiredFields() {
        return new CommChannelTcpPortCreateBuilder.Builder(Optional.empty(), CommChannelType.TCPPORT)
                .withBaudRate(Optional.empty())
                .build();
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
    
    public static JSONObject buildLocalSharedPortAllFields() {        
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
		        .build();
    }
    
    public static Pair<JSONObject, JSONObject> createLocalSharedPortRequiredFields() {        
        return new LocalSharedPortBuilder(Optional.empty(), CommChannelType.LOCAL_SHARED)
        .withPhysicalPort(Optional.empty())
        .withBaudRate(Optional.empty())
        .create();
    }
    
    public static JSONObject buildLocalSharedPortRequiredFields() {        
        return new LocalSharedPortBuilder(Optional.empty(), CommChannelType.LOCAL_SHARED)
        .withPhysicalPort(Optional.empty())
        .withBaudRate(Optional.empty())
        .build();
    }
    
    public static Pair<JSONObject, JSONObject> createTerminalServerAllFields() {
    	return new TerminalServerBuilder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .withIPAddress(Optional.empty())
                .withPortNumber(Optional.empty())
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
    
    public static JSONObject buildTerminalServerAllFields() {
    	return new TerminalServerBuilder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .withIPAddress(Optional.empty())
                .withPortNumber(Optional.empty())
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
                .build();
    }
    
    public static Pair<JSONObject, JSONObject> createTerminalServerRequiredFields() {
    	return new TerminalServerBuilder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .withIPAddress(Optional.empty())
                .withPortNumber(Optional.empty())
                .withBaudRate(Optional.empty())
                .create();
    }
    
    public static JSONObject buildTerminalServerRequiredFields() {
    	return new TerminalServerBuilder(Optional.empty(), CommChannelType.TSERVER_SHARED)
                .withIPAddress(Optional.empty())
                .withPortNumber(Optional.empty())
                .withBaudRate(Optional.empty())
                .build();
    }
    
    public static Pair<JSONObject, JSONObject> createUdpTerminalServerAllFields() {
        return new UdpTerminalServerBuilder(Optional.empty(), CommChannelType.UDPPORT)
        		.withEncryptionKey(Optional.empty())
        		.withPortNumber(Optional.empty())
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
    
    public static JSONObject buildUdpTerminalServerAllFields() {
        return new UdpTerminalServerBuilder(Optional.empty(), CommChannelType.UDPPORT)
        		.withEncryptionKey(Optional.empty())
        		.withPortNumber(Optional.empty())
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
                .build();
    }
    
    public static Pair<JSONObject, JSONObject> createUdpTerminalServerRequiredFields() {
        return new UdpTerminalServerBuilder(Optional.empty(), CommChannelType.UDPPORT)
        		.withPortNumber(Optional.empty())
                .withBaudRate(Optional.empty())
                .create();
    }
    
    public static JSONObject buildUdpTerminalServerRequiredFields() {
        return new UdpTerminalServerBuilder(Optional.empty(), CommChannelType.UDPPORT)
        		.withPortNumber(Optional.empty())
                .withBaudRate(Optional.empty())
                .build();
    }
}