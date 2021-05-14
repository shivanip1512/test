package com.cannontech.web.dev;

public class ZeusEcobeeDataConfiguration {

    private int authenticate = 0;
    private int createDevice = 0;
    private int deleteDevice = 0;
    private int enrollment = 0;
    private int issueDemandResponse = 0;
    private int showUser = 0;
    private int createPushConfiguration = 0;
    private int showPushConfiguration = 0;
    private int getGroup = 0;
    private boolean enableRuntime = false;
    private int cancelDemandResponse = 0;

    public void setZeusEcobeeDataConfiguration(int authenticate, int createDevice, int deleteDevice, int enrollment, int issueDemandResponse, int showUser,
            int createPushConfiguration, int showPushConfiguration, boolean enableRuntime,int getGroup, int cancelDemandResponse) {
        this.authenticate = authenticate;
        this.createDevice = createDevice;
        this.deleteDevice = deleteDevice;
        this.enrollment = enrollment;
        this.issueDemandResponse = issueDemandResponse;
        this.showUser = showUser;
        this.createPushConfiguration = createPushConfiguration;
        this.showPushConfiguration = showPushConfiguration;
        this.getGroup = getGroup;
        this.enableRuntime = enableRuntime;
        this.cancelDemandResponse = cancelDemandResponse;
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

    public int getShowUser() {
        return showUser;
    }

    public int getCreatePushConfiguration() {
        return createPushConfiguration;
    }

    public int getShowPushConfiguration() {
        return showPushConfiguration;
    }

    public boolean isEnableRuntime() {
        return enableRuntime;
    }

    public int getCancelDemandResponse() {
        return cancelDemandResponse;
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

    public void setShowUser(int showUser) {
        this.showUser = showUser;
    }
    
    public void setCreatePushConfiguration(int createPushConfiguration) {
        this.createPushConfiguration = createPushConfiguration;
    }

    public void setShowPushConfiguration(int showPushConfiguration) {
        this.showPushConfiguration = showPushConfiguration;
    }

    public int getGetGroup() {
        return getGroup;
    }

    public void setGetGroup(int getGroup) {
        this.getGroup = getGroup;
    }

    public void setEnableRuntime(boolean enableRuntime) {
        this.enableRuntime = enableRuntime;
    }

    public void setCancelDemandResponse(int cancelDemandResponse) {
        this.cancelDemandResponse = cancelDemandResponse;
    }
}
