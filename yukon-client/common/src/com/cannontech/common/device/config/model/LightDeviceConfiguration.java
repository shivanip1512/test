package com.cannontech.common.device.config.model;

public class LightDeviceConfiguration {
    private final Integer configurationId;
    private final String name;
    private final String description;
    
    public LightDeviceConfiguration(Integer configurationId, String name, String description) {
        this.configurationId = configurationId;
        this.name = name;
        this.description = description;
    }
    
    public Integer getConfigurationId() {
        return configurationId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
}
