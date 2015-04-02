package com.cannontech.common.device.config.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public final class DeviceConfiguration extends LightDeviceConfiguration {
    private final List<DeviceConfigCategory> categories;
    private final Set<PaoType> supportedDeviceTypes;
    
    public DeviceConfiguration(Integer configurationId, 
                               String name, 
                               String description,
                               List<DeviceConfigCategory> categories,
                               Set<PaoType> supportedDeviceTypes) {
        super(configurationId, name, description);
        this.categories = ImmutableList.copyOf(categories);
        this.supportedDeviceTypes = ImmutableSet.copyOf(supportedDeviceTypes);
    }
    
    public List<DeviceConfigCategory> getCategories() {
        return categories;
    }
    
    public Set<PaoType> getSupportedDeviceTypes() {
        return supportedDeviceTypes;
    }
    
    /**
     * Get the DNP category of a configuration.
     * @return a DNP category object if the configuration contains one, null otherwise.
     */
    public DeviceConfigCategory getDnpCategory() {
        for (DeviceConfigCategory category : categories) {
            if (CategoryType.DNP.value().equals(category.getCategoryType())) {
                return category;
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        return String.format("DeviceConfiguration [categories=%s, supportedDeviceTypes=%s]", categories,
            supportedDeviceTypes);
    }

}
