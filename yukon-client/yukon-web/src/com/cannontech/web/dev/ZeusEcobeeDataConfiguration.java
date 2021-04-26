package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private int groupManagement = 0;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, int groupManagement) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.groupManagement = groupManagement;
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

    public int getGroupManagement() {
        return groupManagement;
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

    public void setGroupManagement(int groupManagement) {
        this.groupManagement = groupManagement;
    }

}
