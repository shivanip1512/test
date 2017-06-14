package com.cannontech.simulators.startup.service;

import com.cannontech.simulators.SimulatorType;

/**
 * This is a development testing service that is used to update and access boolean values from the
 * YukonSimulatorSettings table in the database which indicate whether simulators should run
 * automatically when the SimulatorService is started.
 */
public interface SimulatorStartupSettingsService {

    /**
     * Get the startup settings for the given simulator type.
     * @param simulatorType The particular simulator for which we are retrieving startup settings.
     * @return True if the specified simulator is configured to start automatically when Yukon starts, false if the simulator must be started manually.
     */
    public boolean isRunOnStartup(SimulatorType simulatorType);

    /**
     * Upload the given boolean value to the database for the given SimulatorType.
     * @param simulatorType The particular simulator for which we are uploading the startup settings.
     */
    public void saveStartupSettings(boolean runOnStartup, SimulatorType simulatorType);
}
