package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.NmNetworkSimulatorRequest;
import com.cannontech.simulators.message.request.NmNetworkSimulatorRequest.Action;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.NmNetworkSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;

public class NmNetworkSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(NmNetworkSimulatorMessageHandler.class);
    
    @Autowired private NmNetworkSimulatorService service;

    public NmNetworkSimulatorMessageHandler() {
        super(SimulatorType.RFN_NETWORK);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {

            if (simulatorRequest instanceof NmNetworkSimulatorRequest) {
                NmNetworkSimulatorRequest request = (NmNetworkSimulatorRequest) simulatorRequest;
                if (request.getAction() == Action.START) {
                    service.start(request.getSettings());
                } else if (request.getAction() == Action.STOP) {
                    service.stop();
                } else if (request.getAction() == Action.UPDATE_SETTINGS) {
                    service.updateSettings(request.getSettings());
                } else if (request.getAction() == Action.SETUP) {
                    service.setupLocations();
                }
                return new NmNetworkSimulatorResponse(service.getSettings());
            } else {
                throw new IllegalArgumentException(
                    "Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest);
            throw e;
        }
    }
}
