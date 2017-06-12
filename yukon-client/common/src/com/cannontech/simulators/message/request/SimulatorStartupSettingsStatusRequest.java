package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class SimulatorStartupSettingsStatusRequest implements SimulatorRequest {

    private final SimulatorType simType;
    private final SimulatorType downloadType;

    /**
     * SimulatorStrtupSettingsRequests have an updateType because their SimType is SIMULATOR_STARTUP
     * and they also need to know which type of simulator they are retrieving the value of
     * (eg. RFN_METER).
     */
    public SimulatorStartupSettingsStatusRequest(SimulatorType simType, SimulatorType downloadType) {
        this.simType = simType;
        this.downloadType = downloadType;
    }

    @Override
    public SimulatorType getRequestType() {
        return simType;
    }

    public SimulatorType getDownloadType() {
        return downloadType;
    }
}