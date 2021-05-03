package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private int enrollment = 0;
    private int issueDemandResponse = 0;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, int enrollment, int issueDemandResponse) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.enrollment = enrollment;
        this.issueDemandResponse = issueDemandResponse;
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

    public int getIssueDemandResponse() {
        return issueDemandResponse;
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

    public void setIssueDemandResponse(int issueDemandResponse) {
        this.issueDemandResponse = issueDemandResponse;
    }

}