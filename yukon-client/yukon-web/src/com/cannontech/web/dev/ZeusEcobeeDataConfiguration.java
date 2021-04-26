package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private int enrollDevice = 0;
    private int unEnrollDevice = 0;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, int enrollDevice, int unEnrollDevice) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.enrollDevice = enrollDevice;
        this.unEnrollDevice = unEnrollDevice;
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

    public int getDeleteDevice() {
        return deleteDevice;
    }

    public void setDeleteDevice(int deleteDevice) {
        this.deleteDevice = deleteDevice;
    }

    public int getEnrollDevice() {
        return enrollDevice;
    }

    public void setEnrollDevice(int enrollDevice) {
        this.enrollDevice = enrollDevice;
    }

    public int getUnEnrollDevice() {
        return unEnrollDevice;
    }

    public void setUnEnrollDevice(int unEnrollDevice) {
        this.unEnrollDevice = unEnrollDevice;
    }

}
