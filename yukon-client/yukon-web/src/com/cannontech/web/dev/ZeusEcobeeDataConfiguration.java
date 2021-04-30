package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private int enrollment = 0;
    private int createPushConfiguration = 0;
    private int showPushConfiguration = 0;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, int enrollment,
            int createPushConfiguration, int showPushConfiguration) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.enrollment = enrollment;
        this.createPushConfiguration = createPushConfiguration;
        this.showPushConfiguration = showPushConfiguration;
    }

    public int getAuthenticate() {
        return authenticate;
    }

    public int getCreateDevice() {
        return createDevice;
    }

    public int getDeleteDevice() {
        return deleteDevice;
    }

    public int getEnrollment() {
        return enrollment;
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

    public void setEnrollment(int enrollment) {
        this.enrollment = enrollment;
    }

    public int getCreatePushConfiguration() {
        return createPushConfiguration;
    }

    public void setCreatePushConfiguration(int createPushConfiguration) {
        this.createPushConfiguration = createPushConfiguration;
    }

    public int getShowPushConfiguration() {
        return showPushConfiguration;
    }

    public void setShowPushConfiguration(int showPushConfiguration) {
        this.showPushConfiguration = showPushConfiguration;
    }
}
