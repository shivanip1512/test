package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.common.device.config.model.LightDeviceConfiguration;

/**
 * Just a little wrapper for display information on the device configuration home page.
 */
public class DisplayableConfigurationData {
    private final LightDeviceConfiguration configuration;
    private final int numDevices;
    
    public DisplayableConfigurationData(LightDeviceConfiguration configuration, int numDevices) {
        this.configuration = configuration;
        this.numDevices = numDevices;
    }
    
    public String getName() {
        return configuration.getName();
    }
    
    public Integer getConfigurationId() {
        return configuration.getConfigurationId();
    }
    
    public int getNumDevices() {
        return numDevices;
    }
}
