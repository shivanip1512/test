package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private boolean enableRuntime = false;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, boolean enableRuntime) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.enableRuntime = enableRuntime;
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

    public void setAuthenticate(int authenticate) {
        this.authenticate = authenticate;
    }

    public void setCreateDevice(int createDevice) {
        this.createDevice = createDevice;
    }

    public void setDeleteDevice(int deleteDevice) {
        this.deleteDevice = deleteDevice;
    }

    public boolean isEnableRuntime() {
        return enableRuntime;
    }

    public void setEnableRuntime(boolean enableRuntime) {
        this.enableRuntime = enableRuntime;
    }
}
