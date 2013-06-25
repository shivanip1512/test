package com.cannontech.common.device.config.model;

public class LightDeviceConfiguration {
    private final Integer configurationId;
    private final String name;
    
    public LightDeviceConfiguration(Integer configurationId, String name) {
        this.configurationId = configurationId;
        this.name = name;
    }
    
    public Integer getConfigurationId() {
        return configurationId;
    }
    
    public String getName() {
        return name;
    }
}
