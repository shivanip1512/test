package com.cannontech.web.bulk;

import com.cannontech.common.pao.PaoType;

public class MeterProgrammingConfiguration {
    
    private boolean newConfiguration;
    private String existingConfigurationGuid;
    private String name;
    private PaoType paoType;

    public boolean isNewConfiguration() {
        return newConfiguration;
    }
    public void setNewConfiguration(boolean newConfiguration) {
        this.newConfiguration = newConfiguration;
    }
    public String getExistingConfigurationGuid() {
        return existingConfigurationGuid;
    }
    public void setExistingConfigurationGuid(String existingConfigurationGuid) {
        this.existingConfigurationGuid = existingConfigurationGuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PaoType getPaoType() {
        return paoType;
    }
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}
