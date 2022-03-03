package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class SimulatorStartupSettingsStatusRequest implements SimulatorRequest {

    private final SimulatorType affectedSimulator;

    /**
     * Request to get a simulators startup setting from the database.
     * This message is sent with a request type of SIMULATOR_STARTUP so that it will be processed
     * by its own handler.
     * @param affectedSimulator The simulator to get configure.
     */
    public SimulatorStartupSettingsStatusRequest(SimulatorType affectedSimulator) {
        this.affectedSimulator = affectedSimulator;
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

    @Override
    public String toString() {
        return String.format("SimulatorStartupSettingsStatusRequest [affectedSimulator=%s]", affectedSimulator);
    }
}