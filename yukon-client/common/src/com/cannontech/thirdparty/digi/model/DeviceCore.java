package com.cannontech.thirdparty.digi.model;

import org.joda.time.Instant;

public class DeviceCore {
    private int devId;
    private String devMac;
    private String devFirmware;
    private boolean connected;
    private Instant lastTime;
    private Instant commissionTime;
    
    public DeviceCore (int devId,String devMac, String devFirmware,boolean connected, Instant lastTime, Instant commissionTime) {
        this.devId = devId;
        this.devMac = devMac;
        this.devFirmware = devFirmware;
        this.connected = connected;
        this.lastTime = lastTime;
        this.commissionTime = commissionTime;
    }

    public int getDevId() {
        return devId;
    }

    public String getDevMac() {
        return devMac;
    }

    public String getDevFirmware() {
        return devFirmware;
    }

    public boolean isConnected() {
        return connected;
    }
    
    public Instant getLastTime() {
        return lastTime;
    }
    
    public Instant getCommissionTime() {
        return commissionTime;
    }
}