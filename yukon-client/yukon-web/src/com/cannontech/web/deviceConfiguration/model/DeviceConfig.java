package com.cannontech.web.deviceConfiguration.model;

public class DeviceConfig {
    private Integer configId;
    private String configName;
    private String description;
    
    public Integer getConfigId() {
        return configId;
    }
    
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("DeviceConfig [configId=%s, configName=%s, description=%s]", configId, configName, description);
    }

}
