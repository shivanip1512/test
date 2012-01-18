package com.cannontech.thirdparty.digi.model;

import org.joda.time.Instant;

/**
 * Class to represent the data recieved from digi about the EndPoints it knows about.
 */
public class XbeeCore {

    private MacAddress macAddress; //This EndPoint
    private DevConnectwareId devConnectwareId; //Gateway it belongs to
    private NodeAddress xpNetAddr;
    private NodeType xpNodeType;
    private NodeStatus xpStatus;
    private final Instant xpUpdateTime;
    
    public XbeeCore(MacAddress macAddress, DevConnectwareId devConnectwareId, NodeAddress xpNetAddr, 
                     NodeType xpNodeType, NodeStatus xpStatus, Instant xpUpdateTime) {
        this.macAddress = macAddress;
        this.devConnectwareId = devConnectwareId;
        this.xpNetAddr = xpNetAddr;
        this.xpNodeType = xpNodeType;
        this.xpStatus = xpStatus;
        this.xpUpdateTime = xpUpdateTime;
    }

    public MacAddress getMacAddress() {
        return macAddress;
    }
    
    public DevConnectwareId getDevConnectwareId() {
        return devConnectwareId;
    }
    
    public NodeAddress getXpNetAddr() {
        return xpNetAddr;
    }
    
    public NodeType getXpNodeType() {
        return xpNodeType;
    }
    
    public NodeStatus getNodeStatus() {
        return xpStatus;
    }
    
    public Instant getXpUpdateTime() {
        return xpUpdateTime;
    }
}