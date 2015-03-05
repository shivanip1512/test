package com.cannontech.common.device.config.model;

import java.io.Serializable;

public class LightDeviceConfiguration implements Serializable {
    private final static long serialVersionUID = 1L;
    
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configurationId == null) ? 0 : configurationId.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LightDeviceConfiguration other = (LightDeviceConfiguration) obj;
        if (configurationId == null) {
            if (other.configurationId != null)
                return false;
        } else if (!configurationId.equals(other.configurationId))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
