package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate;
    private int createDevice;

    public ZeusEcobeeDataConfiguration(int authenticate, int createDevice) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
    }

    public int getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(int authenticate) {
        this.authenticate = authenticate;
    }

    public int getCreateDevice() {
        return createDevice;
    }

    public void setCreateDevice(int createDevice) {
        this.createDevice = createDevice;
    }

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
    }
}
