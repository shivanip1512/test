package com.cannontech.simulators.message.response;

import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.ivvc.model.IvvcSimulatorStatus;

public class IvvcSimulatorResponse extends SimulatorResponseBase {

    private IvvcSimulatorStatus status;
    private IvvcSimulatorSettings ivvcSimulatorSettings;

    public IvvcSimulatorResponse(IvvcSimulatorStatus status, IvvcSimulatorSettings ivvcSimulatorSettings) {
        this.status = status;
        this.ivvcSimulatorSettings = ivvcSimulatorSettings;
    }
    
    public IvvcSimulatorSettings getSettings() {
        return ivvcSimulatorSettings;
    }

    public IvvcSimulatorStatus getStatus() {
        return status;
    }
}
