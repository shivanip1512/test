package com.cannontech.simulators.dao;

import java.util.Map;

import com.cannontech.simulators.RegulatorVoltageControlMode;

public interface DeviceConfigurationSimulatorDao {

    /**
     * Updates the voltage control mode in the given map with the regulator ids provided as keys 
     */
    void getDeviceVoltageControlMode(Map<Integer, RegulatorVoltageControlMode> regulatorConfigMap);
}
