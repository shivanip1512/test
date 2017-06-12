package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class SimulatorStartupSettingsRequest implements SimulatorRequest {

    private final SimulatorType simType;
    private final SimulatorType uploadType;
    private final boolean runOnStartup;

    /**
     * SimulatorStrtupSettingsRequests have an updateType because their SimType is SIMULATOR_STARTUP
     * and they also need to know which type of simulator they are updating the value of (eg.
     * RFN_METER).
     */
    public SimulatorStartupSettingsRequest(boolean runOnStartup, SimulatorType simType, SimulatorType uploadType) {
        this.simType = simType;
        this.uploadType = uploadType;
        this.runOnStartup = runOnStartup;
    }

    @Override
    public SimulatorType getRequestType() {
        return simType;
    }

    public SimulatorType getUploadType() {
        return uploadType;
    }

    public boolean getRunOnStartup() {
        return runOnStartup;
    }
}