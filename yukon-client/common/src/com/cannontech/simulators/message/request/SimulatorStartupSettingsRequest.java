package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class SimulatorStartupSettingsRequest implements SimulatorRequest {

    private final SimulatorType affectedSimulator;
    private final boolean runOnStartup;

    /**
     * Request to set a simulator to either manual or automatic startup.
     * This message is sent with a request type of SIMULATOR_STARTUP so that it will be processed
     * by its own handler.
     * @param runOnStartup True to set the simulator to run automatically on startup, false to require
     * manual start.
     * @param affectedSimulator The simulator to configure.
     */
    public SimulatorStartupSettingsRequest(boolean runOnStartup, SimulatorType affectedSimulator) {
        this.affectedSimulator = affectedSimulator;
        this.runOnStartup = runOnStartup;
    }

    /**
     * This is SIMULATOR_STARTUP, because these requests have their own handler.
     */
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.SIMULATOR_STARTUP;
    }

    public SimulatorType getAffectedSimulator() {
        return affectedSimulator;
    }

    public boolean isRunOnStartup() {
        return runOnStartup;
    }
}