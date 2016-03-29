package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class RfnMeterDataSimulatorStartRequest implements SimulatorRequest {

    private SimulatorSettings settings;

    public RfnMeterDataSimulatorStartRequest(SimulatorSettings settings) {
        this.settings = settings;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }
}
