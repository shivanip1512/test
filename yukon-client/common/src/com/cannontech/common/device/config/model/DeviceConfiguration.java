package com.cannontech.common.device.config.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoType;

public final class DeviceConfiguration extends LightDeviceConfiguration {
    private final List<DeviceConfigCategory> categories;
    private final Set<PaoType> supportedDeviceTypes;
    
    public DeviceConfiguration(Integer configurationId, 
                               String name, 
                               String description,
                               List<DeviceConfigCategory> categories,
                               Set<PaoType> supportedDeviceTypes) {
        super(configurationId, name, description);
        this.categories = categories;
        this.supportedDeviceTypes = supportedDeviceTypes;
    }
    
    public List<DeviceConfigCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }
    
    public Set<PaoType> getSupportedDeviceTypes() {
        return Collections.unmodifiableSet(supportedDeviceTypes);
    }
}
