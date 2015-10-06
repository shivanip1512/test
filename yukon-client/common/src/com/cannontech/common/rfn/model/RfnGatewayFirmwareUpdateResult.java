package com.cannontech.common.rfn.model;

/**
 * Information for a single gateway in a firmware upgrade.
 */
public class RfnGatewayFirmwareUpdateResult {
    private int updateId;
    private int gatewayPaoId;
    private String originalVersion;
    private String newVersion;
    private String updateServerUrl;
    private GatewayFirmwareUpdateStatus status;
    
    public int getUpdateId() {
        return updateId;
    }
    
    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }
    
    public int getGatewayPaoId() {
        return gatewayPaoId;
    }
    
    public void setGatewayPaoId(int gatewayPaoId) {
        this.gatewayPaoId = gatewayPaoId;
    }
    
    /**
     * @return The firmware version of the gateway prior to the upgrade.
     */
    public String getOriginalVersion() {
        return originalVersion;
    }
    
    public void setOriginalVersion(String originalVersion) {
        this.originalVersion = originalVersion;
    }
    
    /**
     * @return The firmware version that was available on the upgrade server at the time the upgrade was attempted.
     */
    public String getNewVersion() {
        return newVersion;
    }
    
    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }
    
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }
    
    public void setUpdateServerUrl(String updateServerUrl) {
        this.updateServerUrl = updateServerUrl;
    }
    
    public GatewayFirmwareUpdateStatus getStatus() {
        return status;
    }
    
    public void setStatus(GatewayFirmwareUpdateStatus status) {
        this.status = status;
    }
    
}
