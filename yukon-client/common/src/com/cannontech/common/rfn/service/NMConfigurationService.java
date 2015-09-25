package com.cannontech.common.rfn.service;

public interface NMConfigurationService {

    /**
     * Checks if the current version of the firmware supports firmware update feature
     */
    public boolean isFirmwareUpdateSupported();
    
    /**
     * Checks if the current version of the firmware supports voltage profiling feature
     */
    public boolean isNewVoltageProfileUpdateSupported();
}
