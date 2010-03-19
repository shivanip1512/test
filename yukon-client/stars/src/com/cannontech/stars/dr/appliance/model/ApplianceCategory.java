package com.cannontech.stars.dr.appliance.model;

import com.cannontech.stars.webconfiguration.model.WebConfiguration;


public class ApplianceCategory {
    private int applianceCategoryId;
    private String name;
    private ApplianceTypeEnum applianceType;
    private boolean consumerSelectable;

    private WebConfiguration webConfiguration;

    public ApplianceCategory() {
        applianceCategoryId = 0;
        consumerSelectable = true;
        webConfiguration = new WebConfiguration();
    }

    public ApplianceCategory(int applianceCategoryId, String name,
            ApplianceTypeEnum applianceType, boolean consumerSelectable,
            WebConfiguration webConfiguration) {
        this.applianceCategoryId = applianceCategoryId;
        this.name = name;
        this.applianceType = applianceType;
        this.consumerSelectable = consumerSelectable;
        this.webConfiguration = webConfiguration;
    }


    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApplianceType(ApplianceTypeEnum applianceType) {
        this.applianceType = applianceType;
    }

    public ApplianceTypeEnum getApplianceType() {
        return applianceType;
    }

    public boolean isConsumerSelectable() {
        return consumerSelectable;
    }

    public void setConsumerSelectable(boolean consumerSelectable) {
        this.consumerSelectable = consumerSelectable;
    }

    public WebConfiguration getWebConfiguration() {
        return webConfiguration;
    }

    public void setWebConfiguration(WebConfiguration webConfiguration) {
        this.webConfiguration = webConfiguration;
    }

    public int getWebConfigurationId() {
        return webConfiguration.getConfigurationId();
    }

    public void setWebConfigurationId(int webConfigurationId) {
        webConfiguration.setConfigurationId(webConfigurationId);
    }

    public String getIcon() {
        return webConfiguration.getLogoLocation();
    }

    public void setIcon(String icon) {
        webConfiguration.setLogoLocation(icon);
    }

    public ApplianceCategoryIcon getIconEnum() {
        return ApplianceCategoryIcon.getByFilename(getIcon());
    }

    public String getDescription() {
        return webConfiguration.getDescription();
    }

    public void setDescription(String description) {
        webConfiguration.setDescription(description);
    }

    public String getDisplayName() {
        return webConfiguration.getAlternateDisplayName();
    }

    public void setDisplayName(String displayName) {
        webConfiguration.setAlternateDisplayName(displayName);
    }


    public String getCategoryLabel() {
        String retVal = webConfiguration.getAlternateDisplayName();
        if (retVal != null) {
            retVal = name;
        }
        return retVal;
    }

    /**
     * Get the web configuration "logoLocation" variable"
     * @deprecated use getLogoLocation().
     */
    public String getLogoPath() {
        return webConfiguration.getLogoLocation();
    }

    /**
     * @deprecated use getApplianceType 
     */
    public ApplianceTypeEnum getApplianceTypeEnum() {
        return applianceType;
    }
}
