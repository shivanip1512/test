package com.cannontech.simulators.startup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.startup.service.SimulatorStartupSettingsService;

/**
 * Attempts to save boolean values to the database and access boolean values from the database for 
 * the given SimulatorTypes.
 * @throws IllegalArgumentException if the SimulatorType is not found in any of the YukonSimulatorSettingsKeys.
 */
public class SimulatorStartupSettingsServiceImpl implements SimulatorStartupSettingsService {

    @Autowired
    private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    @Override
    public boolean isRunOnStartup(SimulatorType simulatorType) {
        for (YukonSimulatorSettingsKey key : YukonSimulatorSettingsKey.values()) {
            if (key.name().equals(simulatorType.name() + "_SIMULATOR_RUN_ON_STARTUP")) {
                return yukonSimulatorSettingsDao.getBooleanValue(key);
            }
        }
        throw new IllegalArgumentException("Invalid SimulatorType passed to SimulatorStartupSettingsService, unable to get SimulatorStartupSettings for: " + simulatorType.name());
    }

    @Override
    public void saveStartupSettings(boolean runOnStartup, SimulatorType simulatorType) {
        for (YukonSimulatorSettingsKey key : YukonSimulatorSettingsKey.values()) {
            if (key.name().equals(simulatorType.name() + "_SIMULATOR_RUN_ON_STARTUP")) {
                yukonSimulatorSettingsDao.setValue(key, runOnStartup);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid SimulatorType passed to SimulatorStartupSettingsService, unable to update SimulatorStartupSettings for: " + simulatorType);
    }
}
