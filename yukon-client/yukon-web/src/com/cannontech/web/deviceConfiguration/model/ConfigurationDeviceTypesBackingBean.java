package com.cannontech.web.deviceConfiguration.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;

public class ConfigurationDeviceTypesBackingBean {
    private Map<PaoType, Boolean> supportedTypes = new HashMap<>();
    
    public ConfigurationDeviceTypesBackingBean() {
    }
    
    public ConfigurationDeviceTypesBackingBean(Set<PaoDefinition> definitions) {
        for (PaoDefinition definition : definitions) {
            supportedTypes.put(definition.getType(), false);
        }
    }
    
    public Map<PaoType, Boolean> getSupportedTypes() {
        return supportedTypes;
    }
    
    public void setSupportedTypes(Map<PaoType, Boolean> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }
}
