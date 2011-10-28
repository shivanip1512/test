package com.cannontech.thirdparty.digi.model;

import org.joda.time.Instant;

/**
 * Class to represent the data recieved from digi about the EndPoints it knows about.
 */
public class XbeeCore {

    private String macAddress; //This EndPoint
    private String devConnectwareId; //Gateway it delongs to
    private int xpNetAddr;
    private int xpNodeType; // 0 = coordinator, 1 = router (UPRO is this), 2 = End Node
    private int xpDiscoveryIndex;
    private boolean xpStatus;
    private Instant xpUpdateTime;

    public XbeeCore(String macAddress, String devConnectwareId, int xpNetAddr, int xpNodeType,
                     int xpDiscoveryIndex, boolean xpStatus, Instant xpUpdateTime) {
        this.macAddress = macAddress;
        this.devConnectwareId = devConnectwareId;
        this.xpNetAddr = xpNetAddr;
        this.xpNodeType = xpNodeType;
        this.xpDiscoveryIndex = xpDiscoveryIndex;
        this.xpStatus = xpStatus;
        this.xpUpdateTime = xpUpdateTime;
    }

    public String getMacAddress() {
        return macAddress;
    }
    
    public String getDevConnectwareId() {
        return devConnectwareId;
    }
    
    public int getXpNetAddr() {
        return xpNetAddr;
    }
    
    public int getXpNodeType() {
        return xpNodeType;
    }
    
    
    public int getXpDiscoveryIndex() {
        return xpDiscoveryIndex;
    }
    
    public boolean isConnected() {
        return xpStatus;
    }
    
    public Instant getXpUpdateTime() {
        return xpUpdateTime;
    }
}
