package com.cannontech.simulators.message.request;

import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class IvvcSimulatorSettingsChangedRequest implements SimulatorRequest {

    private IvvcSimulatorSettings ivvcSimulatorSettings;

    public IvvcSimulatorSettingsChangedRequest(IvvcSimulatorSettings ivvcSimulatorSettings) {
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
        return String.format("IvvcSimulatorSettingsChangedRequest [ivvcSimulatorSettings=%s]", ivvcSimulatorSettings);
    }
}