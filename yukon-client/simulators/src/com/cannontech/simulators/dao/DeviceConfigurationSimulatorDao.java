package com.cannontech.simulators.dao;

import java.util.Map;

import com.cannontech.simulators.RegulatorVoltageControlMode;

public interface DeviceConfigurationSimulatorDao {

    /**
     * Returns the voltage control mode for the regulator ids provided
     * 
     * @param deviceIds as regulatorIds
     * @returns the Map with regulatorId and voltageControlModes mappings
     */
    void getDeviceVoltageControlMode(Map<Integer, RegulatorVoltageControlMode> regulatorConfigMap);
}
