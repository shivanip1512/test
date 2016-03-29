package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;

public class RfnMeterDataSimulatorStatusResponse extends SimulatorResponseBase {

    private RfnDataSimulatorStatus status;
    private SimulatorSettings settings;

    public RfnMeterDataSimulatorStatusResponse(RfnDataSimulatorStatus status, SimulatorSettings settings) {
        this.status = status;
        this.settings = settings;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }

    public RfnDataSimulatorStatus getStatus() {
        return status;
    }
}
