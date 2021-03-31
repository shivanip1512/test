package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate;
    private int createDevice;
    private int deleteDevice;

    public ZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
    }

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
    }

    public int getDeleteDevice() {
        return deleteDevice;
    }

    public int getAuthenticate() {
        return authenticate;
    }

    public int getCreateDevice() {
        return createDevice;
    }

}
