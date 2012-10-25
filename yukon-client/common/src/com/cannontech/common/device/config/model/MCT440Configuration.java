package com.cannontech.common.device.config.model;


import com.cannontech.common.device.config.dao.ConfigurationType;

public class MCT440Configuration extends ConfigurationBase {

    public MCT440Configuration(){
    }
    
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.MCT440;
    }
}
