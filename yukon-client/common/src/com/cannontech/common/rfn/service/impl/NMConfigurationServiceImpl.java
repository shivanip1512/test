package com.cannontech.common.rfn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;
import com.cannontech.common.rfn.service.NMConfigurationService;

public class NMConfigurationServiceImpl implements NMConfigurationService {
    
    @Autowired private ConfigurationSource configurationSource;
    
    @Override
    public boolean isFirmwareUpdateSupported() {
        if(configurationSource.getDouble(MasterConfigDouble.NM_COMPATIBILITY) == 7.0) {
            return true;
        } 
        return false;
    }

    @Override
    public boolean isNewVoltageProfileUpdateSupported() {
        if(configurationSource.getDouble(MasterConfigDouble.NM_COMPATIBILITY) == 7.0) {
            return true;
        } 
        return false;
    }
}
