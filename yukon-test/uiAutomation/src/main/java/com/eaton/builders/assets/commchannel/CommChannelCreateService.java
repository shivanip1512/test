package com.eaton.builders.assets.commchannel;

import org.javatuples.Pair;
import org.json.JSONObject;

public class CommChannelCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateCommChannelTCPPortDefault() {
        return CommChannelTcpPortCreateBuilder.buildDefaultCommChannelTcpPort().create();
    }
    
    public static Pair<JSONObject, JSONObject> buildAndCreateCommChannelLocalSharedPortDefault() {
        return CommChannelLocalSharedPortCreateBuilder.buildDefaultCommChannelLocalSharedPort().create();
    }
    
    public static Pair<JSONObject, JSONObject> buildAndCreateCommChannelTerminalServerDefault() {
        return CommChannelTerminalServerCreateBuilder.buildDefaultCommChannelTerminalServer().create();
    }
    
    public static Pair<JSONObject, JSONObject> buildAndCreateCommChannelUdpTerminalServerDefault() {
        return CommChannelUdpTerminalServerCreateBuilder.buildDefaultCommChannelUdpTerminalServer().create();
    }
}