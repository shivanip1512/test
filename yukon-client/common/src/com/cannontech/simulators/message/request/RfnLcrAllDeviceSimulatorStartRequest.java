package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class RfnLcrAllDeviceSimulatorStartRequest implements SimulatorRequest {

    private SimulatorSettings settings;

    public RfnLcrAllDeviceSimulatorStartRequest(SimulatorSettings settings) {
        this.settings = settings;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_LCR;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return String.format("RfnLcrAllDeviceSimulatorStartRequest [settings=%s]", settings);
    }
}
