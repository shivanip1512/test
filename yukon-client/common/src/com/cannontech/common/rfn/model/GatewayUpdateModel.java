package com.cannontech.common.rfn.model;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.gateway.Authentication;

public class GatewayUpdateModel {
    private static final Logger log = YukonLogManager.getLogger(GatewayUpdateModel.class);
    
    private int id;
    private String name;
    private String updateServerUrl;
    private Authentication updateServerLogin;
    private boolean useDefault;
    private String serialNumber;
    private String currentVersion;
    private String availableVersion;
    private boolean sendNow;

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
    public boolean isSendNow() {
        return sendNow;
    }
    public void setSendNow(boolean sendNow) {
        this.sendNow = sendNow;
    }

    public boolean isUpdateAvailable() {
        return availableVersion != null;
    }

    public static GatewayUpdateModel of(RfnGateway gateway) {

        GatewayUpdateModel updateServer = new GatewayUpdateModel();

        updateServer.setId(gateway.getPaoIdentifier().getPaoId());
        updateServer.setName(gateway.getName());
        updateServer.setUpdateServerUrl(gateway.getData().getUpdateServerUrl());
        updateServer.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());
        updateServer.setSerialNumber(gateway.getRfnIdentifier().getSensorSerialNumber());
        updateServer.setCurrentVersion(gateway.getData().getReleaseVersion());
        updateServer.setAvailableVersion(gateway.getUpgradeVersion());

        return updateServer;
    }
    
    public boolean isUpgradeable() {
        try {
            GatewayFirmwareVersion currentVersion = GatewayFirmwareVersion.parse(this.currentVersion);
            GatewayFirmwareVersion availableVersion = GatewayFirmwareVersion.parse(this.availableVersion);
            int comparison = currentVersion.compareTo(availableVersion);
            log.debug("Comparing gateway firmware versions: " + currentVersion + " <=> " + availableVersion + " = " + comparison);
            return comparison <= 0;
        } catch (Exception e) {
            //If there's any reason we can't compare the versions, assume it's an invalid upgrade.
            log.debug("Comparison failed", e);
            return false;
        }
    }
}