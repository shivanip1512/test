package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnNetworkManagerSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnNetworkManagerSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.RfnNetworkManagerSimulatorStartRequest;
import com.cannontech.simulators.message.request.RfnNetworkManagerSimulatorStatusRequest;
import com.cannontech.simulators.message.request.RfnNetworkManagerSimulatorStopRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.RfnNetworkManagerSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class RfnNetworkManagerSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnNetworkManagerSimulatorMessageHandler.class);
    @Autowired private RfnNetworkManagerSimulatorService simulator;

    public RfnNetworkManagerSimulatorMessageHandler() {
        super(SimulatorType.RFN_METER_NETWORK);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            
            if (simulatorRequest instanceof RfnNetworkManagerSimulatorStartRequest) {
                RfnNetworkManagerSimulatorStartRequest request = (RfnNetworkManagerSimulatorStartRequest) simulatorRequest;
                simulator.startMeterDisconnectReply(request.getSettings());
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnNetworkManagerSimulatorStopRequest) {
                simulator.stopMeterDisconnectReply();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnNetworkManagerSimulatorStatusRequest) {
                RfnNetworkManagerSimulatorSettings settings = simulator.getCurrentSettings();
                RfnDataSimulatorStatus status = simulator.getStatus();
                return new RfnNetworkManagerSimulatorStatusResponse(status, settings);
            } 
            else {
                throw new IllegalArgumentException(
                    "Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest);
            throw e;
        }
    }
}
