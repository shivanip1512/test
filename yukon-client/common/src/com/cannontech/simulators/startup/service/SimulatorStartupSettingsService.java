package com.cannontech.simulators.startup.service;

import com.cannontech.simulators.SimulatorType;

/**
 * This is a development testing service that is used to update and access boolean values from the
 * YukonSimulatorSettings table in the database which indicate whether simulators should run
 * automatically when the SimulatorService is started.
 */
public interface SimulatorStartupSettingsService {

    /**
     * Get the startup settings boolean from the database for the given SimulatorType.
     * @param SimulatorType
     * @return True or false
     */
    public boolean getRunOnStartup(SimulatorType downloadType);

    /**
     * Upload the given boolean value to the database for the given SimulatorType.
     * @param a boolean and SimulatorType
     */
    public void uploadSimulatorStartupSettingsToDb(boolean runOnStartup, SimulatorType uploadType);
}
