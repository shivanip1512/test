package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.gateway.Authentication;

public class GatewayUpdateServer {

    private int id;
    private String name;
    private String updateServerUrl;
    private Authentication updateServerLogin;
    private boolean useDefault;
    private String serialNumber;
    private String currentVersion;
    private String availableVersion;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }
    public void setUpdateServerUrl(String updateServerUrl) {
        this.updateServerUrl = updateServerUrl;
    }
    public Authentication getUpdateServerLogin() {
        return updateServerLogin;
    }
    public void setUpdateServerLogin(Authentication updateServerLogin) {
        this.updateServerLogin = updateServerLogin;
    }
    public boolean isUseDefault() {
        return useDefault;
    }
    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getCurrentVersion() {
        return currentVersion;
    }
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
    public String getAvailableVersion() {
        return availableVersion;
    }
    public void setAvailableVersion(String availableVersion) {
        this.availableVersion = availableVersion;
    }

    public static GatewayUpdateServer of(RfnGateway gateway) {

        GatewayUpdateServer updateServer = new GatewayUpdateServer();

        updateServer.setId(gateway.getPaoIdentifier().getPaoId());
        updateServer.setName(gateway.getName());
        updateServer.setUpdateServerUrl(gateway.getData().getUpdateServerUrl());
        updateServer.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());
        updateServer.setSerialNumber(gateway.getRfnIdentifier().getSensorSerialNumber());
        updateServer.setCurrentVersion(gateway.getData().getReleaseVersion());

        return updateServer;
    }

}
