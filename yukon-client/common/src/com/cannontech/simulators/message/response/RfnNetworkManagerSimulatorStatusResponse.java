package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnNetworkManagerSimulatorSettings;

public class RfnNetworkManagerSimulatorStatusResponse extends SimulatorResponseBase {

    private RfnDataSimulatorStatus status;
    private RfnNetworkManagerSimulatorSettings settings;

    public RfnNetworkManagerSimulatorStatusResponse(RfnDataSimulatorStatus status, RfnNetworkManagerSimulatorSettings settings) {
        this.status = status;
        this.settings = settings;
    }

    public RfnNetworkManagerSimulatorSettings getSettings() {
        return settings;
    }

    public RfnDataSimulatorStatus getStatus() {
        return status;
    }
    
}
