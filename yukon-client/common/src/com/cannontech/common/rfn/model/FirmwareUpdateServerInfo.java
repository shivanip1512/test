package com.cannontech.common.rfn.model;

/**
 * Data object that encapsulates information about an RFN firmware update server.
 */
public class FirmwareUpdateServerInfo {
    private String updateServerUrl;
    private String releaseVersion;
    private String availableVersion;
    
    public FirmwareUpdateServerInfo(String updateServerUrl, String releaseVersion, String availableVersion) {
        this.updateServerUrl = updateServerUrl;
        this.releaseVersion = releaseVersion;
        this.availableVersion = availableVersion;
    }
    
    public String getUpdateServerUrl() {
        return updateServerUrl;
    }
    
    public String getReleaseVersion() {
        return releaseVersion;
    }
    
    public String getAvailableVersion() {
        return availableVersion;
    }
    
}
