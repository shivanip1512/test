package com.cannontech.simulators.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.ivvc.model.IvvcSimulatorStatus;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.ivvc.IvvcSimulatorService;
import com.cannontech.simulators.message.request.IvvcSimulatorSettingsChangedRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStartRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStatusRequest;
import com.cannontech.simulators.message.request.IvvcSimulatorStopRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.IvvcSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class IvvcSimulatorMessageHandler extends SimulatorMessageHandler {

    @Autowired
    private IvvcSimulatorService ivvcSimulatorService;

    public IvvcSimulatorMessageHandler() {
        super(SimulatorType.IVVC);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        if (simulatorRequest instanceof IvvcSimulatorStartRequest) {
            return new SimulatorResponseBase(ivvcSimulatorService
                .start(((IvvcSimulatorStartRequest) simulatorRequest).getSettings()));
        } else if (simulatorRequest instanceof IvvcSimulatorStopRequest) {
            ivvcSimulatorService.stop();
            return new SimulatorResponseBase(true);
        } else if (simulatorRequest instanceof IvvcSimulatorStatusRequest) {
            IvvcSimulatorStatus status = new IvvcSimulatorStatus();
            status.setRunning(ivvcSimulatorService.isRunning());
            IvvcSimulatorSettings ivvcSimulatorSettings =
                ivvcSimulatorService.getCurrentSettings();
            return new IvvcSimulatorResponse(status, ivvcSimulatorSettings);
        } else if (simulatorRequest instanceof IvvcSimulatorSettingsChangedRequest) {
            ivvcSimulatorService.saveSettings(((IvvcSimulatorSettingsChangedRequest) simulatorRequest).getSettings());
            return new SimulatorResponseBase(true);
        } else {
            throw new IllegalArgumentException("Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());
        }
    }
}
