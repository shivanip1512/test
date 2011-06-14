package com.cannontech.thirdparty.model;

public class DeviceCore {
    private int devId;
    private String devMac;
    private String devFirmware;
    private boolean connected;

    public DeviceCore (int devId,String devMac, String devFirmware,boolean connected) {
        this.devId = devId;
        this.devMac = devMac;
        this.devFirmware = devFirmware;
        this.connected = connected;
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
    
}