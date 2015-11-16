package com.cannontech.web.deviceConfiguration.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoDefinition;

public class DeviceConfigTypes {
    private int configId;
    private Map<PaoType, Boolean> supportedTypes = new LinkedHashMap<>();
    private Map<String, Collection<PaoType>> typesByCategory = new HashMap<>();
    private List<PaoType> availableTypes = new ArrayList<>();
    
    public DeviceConfigTypes() {
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
    public static DeviceConfigTypes fromPaoTypes(Set<PaoDefinition> definitions, 
                                                                   Set<PaoType> paoTypes) {
        DeviceConfigTypes backingBean = new DeviceConfigTypes();

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
    public static DeviceConfigTypes fromPaoDefinitions(Set<PaoDefinition> definitions) {
        return fromPaoTypes(definitions, null);
    }
    
    public Map<PaoType, Boolean> getSupportedTypes() {
        return supportedTypes;
    }
    
    public void setSupportedTypes(Map<PaoType, Boolean> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }
    
    public Map<String, Collection<PaoType>> getTypesByCategory() {
        return typesByCategory;
    }
    
    public void setTypesByCategory(Map<String, Collection<PaoType>> typesByCategory) {
        this.typesByCategory = typesByCategory;
    }
    
    public List<PaoType> getAvailableTypes() {
        return availableTypes;
    }
    
    public void setAvailableTypes(List<PaoType> availableTypes) {
        this.availableTypes = availableTypes;
    }
    
    public int getConfigId() {
        return configId;
    }
    
    public void setConfigId(int configId) {
        this.configId = configId;
    }
    
    /**
     * Check to see if the configuration supports no device types.
     * @return false if any pao type in the supported types map for this configuration has a value of true,
     *      true otherwise.
     */
    public boolean isSupportedTypesEmpty() {
        for (Entry<PaoType, Boolean> entry : supportedTypes.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == true) {
                return false;
            }
        }
        
        return true;
    }
}
