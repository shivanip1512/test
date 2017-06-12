package com.cannontech.simulators.startup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.startup.service.SimulatorStartupSettingsService;

/**
 * Attempts to update and access boolean values from the database for the given SimulatorTypes.
 * @throws IllegalArgumentException if the SimulatorType is not found in any of the YukonSimulatorSettingsKeys.
 */
public class SimulatorStartupSettingsServiceImpl implements SimulatorStartupSettingsService {

    @Autowired
    private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    @Override
    public boolean getRunOnStartup(SimulatorType downloadType) {
        for (YukonSimulatorSettingsKey key : YukonSimulatorSettingsKey.values()) {
            if (key.name().contains(downloadType.name()) && key.name().contains("RUN_ON_STARTUP")) {
                return yukonSimulatorSettingsDao.getBooleanValue(key);
            }
        }
        throw new IllegalArgumentException("Invalid SimulatorType passed to SimulatorStartupSettingsService, unable to get SimulatorStartupSettings for: " + downloadType.name());
    }

    @Override
    public void uploadSimulatorStartupSettingsToDb(boolean runOnStartup, SimulatorType uploadType) {
        for (YukonSimulatorSettingsKey key : YukonSimulatorSettingsKey.values()) {
            if (key.name().contains(uploadType.name()) && key.name().contains("RUN_ON_STARTUP")) {
                System.out.println(uploadType);
                yukonSimulatorSettingsDao.setValue(key, runOnStartup);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid SimulatorType passed to SimulatorStartupSettingsService, unable to update SimulatorStartupSettings for: " + uploadType);
    }
}
