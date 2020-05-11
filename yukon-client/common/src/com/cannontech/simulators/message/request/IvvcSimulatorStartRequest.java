package com.cannontech.simulators.message.request;

import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class IvvcSimulatorStartRequest implements SimulatorRequest {

    private IvvcSimulatorSettings ivvcSimulatorSettings;

    public IvvcSimulatorStartRequest(IvvcSimulatorSettings ivvcSimulatorSettings) {
        this.ivvcSimulatorSettings = ivvcSimulatorSettings;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.IVVC;
    }

    public IvvcSimulatorSettings getSettings() {
        return ivvcSimulatorSettings;
    }

    @Override
    public String toString() {
        return String.format("IvvcSimulatorStartRequest [ivvcSimulatorSettings=%s]", ivvcSimulatorSettings);
    }
}