package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStartRequest;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStatusRequest;
import com.cannontech.simulators.message.request.RfnMeterDataSimulatorStopRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.RfnMeterDataSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class RfnMeterDataSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterDataSimulatorMessageHandler.class);
    @Autowired private RfnMeterDataSimulatorService rfnMeterDataSimulatorService;

    public RfnMeterDataSimulatorMessageHandler() {
        super(SimulatorType.RFN_METER);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {

            if (simulatorRequest instanceof RfnMeterDataSimulatorStartRequest) {
                RfnMeterDataSimulatorStartRequest request = (RfnMeterDataSimulatorStartRequest) simulatorRequest;
                if(request.isTest()){
                    rfnMeterDataSimulatorService.testSimulator(request.getSettings());
                }else{
                    rfnMeterDataSimulatorService.startSimulator(request.getSettings());
                }
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnMeterDataSimulatorStopRequest) {
                rfnMeterDataSimulatorService.stopSimulator();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnMeterDataSimulatorStatusRequest) {
                SimulatorSettings settings = rfnMeterDataSimulatorService.getCurrentSettings();
                RfnDataSimulatorStatus status = rfnMeterDataSimulatorService.getStatus();
                return new RfnMeterDataSimulatorStatusResponse(status, settings);
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
