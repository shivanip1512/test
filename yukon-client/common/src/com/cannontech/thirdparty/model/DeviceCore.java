package com.cannontech.thirdparty.model;

import org.joda.time.Instant;

public class DeviceCore {
    private int devId;
    private String devMac;
    private String devFirmware;
    private boolean connected;
    private Instant lastTime;
    
    public DeviceCore (int devId,String devMac, String devFirmware,boolean connected, Instant lastTime ) {
        this.devId = devId;
        this.devMac = devMac;
        this.devFirmware = devFirmware;
        this.connected = connected;
        this.lastTime = lastTime;
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
    
}