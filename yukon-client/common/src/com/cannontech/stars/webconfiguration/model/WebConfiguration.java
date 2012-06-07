package com.cannontech.stars.webconfiguration.model;

public class WebConfiguration {
    private int configurationId; 
    private String logoLocation;
    private String description;
    private String alternateDisplayName;
    private String url;

    public WebConfiguration() {
    }

    public WebConfiguration(int configurationId, String logoLocation,
            String description, String alternateDisplayName, String url) {
        this.configurationId = configurationId;
        this.logoLocation = logoLocation;
        this.description = description;
        this.alternateDisplayName = alternateDisplayName;
        this.url = url;
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public String getLogoLocation() {
        return logoLocation;
    }

    public void setLogoLocation(String logoLocation) {
        this.logoLocation = logoLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlternateDisplayName() {
        return alternateDisplayName;
    }

    public void setAlternateDisplayName(String alternateDisplayName) {
        this.alternateDisplayName = alternateDisplayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
