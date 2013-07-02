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
    
    /**
     * This method should be used when constructing a backing bean from an already existing device configuration.
     * It populates the supported types map with all pao types contained in the provided set of pao definitions and
     * sets all supported values to false except for the values of those pao types provided in the paoTypes param, 
     * whose values are set to true.
     * 
     * Use {@link #fromPaoDefinitions(Set)} when creating a bean for a new device configuration.
     * 
     * @param definitions the pao definitions whose pao types will populate the supported types map.
     * @param paoTypes the set of pao types supported by an already existing device configuration
     * @return a backing bean whose supported types contains all pao types present in the provided definitions with 
     *      supported values of true for all of the provided pao types.
     */
    public static ConfigurationDeviceTypesBackingBean fromPaoTypes(Set<PaoDefinition> definitions, 
                                                                   Set<PaoType> paoTypes) {
        ConfigurationDeviceTypesBackingBean backingBean = new ConfigurationDeviceTypesBackingBean();

        for (PaoDefinition definition : definitions) {
            backingBean.supportedTypes.put(definition.getType(), false);
        }
        
        if (paoTypes != null) {
            for (PaoType paoType : paoTypes) {
                backingBean.supportedTypes.put(paoType, true);
            }
        }
        
        return backingBean;
    }
    
    /**
     * This method should be used when constructing a backing bean for use in creating a new device configuration.
     * It populates the supported types map with all pao types contained in the provided set of pao definitions and
     * sets their supported values to false.
     * @param definitions the pao definitions whose pao types will populate the supported types map
     * @return a backing bean whose supported types contains entries for all of the pao types in the provided
     *      pao definitions and whose supported values are all false;
     */
    public static ConfigurationDeviceTypesBackingBean fromPaoDefinitions(Set<PaoDefinition> definitions) {
        return fromPaoTypes(definitions, null);
    }
    
    public Map<PaoType, Boolean> getSupportedTypes() {
        return supportedTypes;
    }
    
    public void setSupportedTypes(Map<PaoType, Boolean> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }
}
