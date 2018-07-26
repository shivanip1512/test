package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterReadAndControlSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.ModifyRfnMeterReadAndControlSimulatorRequest;
import com.cannontech.simulators.message.request.RfnMeterReadAndControlSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.RfnMeterReadAndControlSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class RfnMeterReadAndControlSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadAndControlSimulatorMessageHandler.class);
    @Autowired private RfnMeterReadAndControlSimulatorService simulator;

    public RfnMeterReadAndControlSimulatorMessageHandler() {
        super(SimulatorType.RFN_METER_READ_CONTROL);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest request) {
        
        try {
            if (request instanceof ModifyRfnMeterReadAndControlSimulatorRequest) {
                ModifyRfnMeterReadAndControlSimulatorRequest simulatorRequest = (ModifyRfnMeterReadAndControlSimulatorRequest) request;
                
                // Stop any RfnMeterReadAndControl simulators with a stop request
                stopSimulators(simulatorRequest);
                
                // Start any RfnMeterReadAndControl simulators that have non-null start settings in the request
                boolean success = startSimulators(simulatorRequest);
                
                return new SimulatorResponseBase(success);
            } else if (request instanceof RfnMeterReadAndControlSimulatorStatusRequest) {
                return getStatus();
            } else {
                throw new IllegalArgumentException("Unsupported request type received: " + request.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + request);
            throw e;
        }
    }
    
    private void stopSimulators(ModifyRfnMeterReadAndControlSimulatorRequest request) {
        if (request.isStopReadReply()) {
            simulator.stopMeterReadReply();
        }
        if (request.isStopDisconnectReply()) {
            simulator.stopMeterDisconnectReply();;
        }
    }
    
    private boolean startSimulators(ModifyRfnMeterReadAndControlSimulatorRequest request) {
        boolean totalSuccess = true;
        
        RfnMeterReadAndControlReadSimulatorSettings readSettings = request.getReadSettings();
        if ((!simulator.isMeterReadReplyActive() || simulator.isMeterReadReplyStopping()) && (readSettings != null)) {
            boolean success = simulator.startMeterReadReply(readSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings = request.getDisconnectSettings();
        if ((!simulator.isMeterDisconnectReplyActive() || simulator.isMeterDisconnectReplyStopping()) && (disconnectSettings != null)) {
            boolean success = simulator.startMeterDisconnectReply(disconnectSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        return totalSuccess;
    }
    
    private RfnMeterReadAndControlSimulatorStatusResponse getStatus() {
        RfnMeterReadAndControlSimulatorStatusResponse response = new RfnMeterReadAndControlSimulatorStatusResponse();
        
        response.setReadSettings(simulator.getReadSettings());
        response.setMeterReadReplyActive(simulator.isMeterReadReplyActive() && (!simulator.isMeterReadReplyStopping()));
        
        response.setDisconnectSettings(simulator.getDisconnectSettings());
        response.setMeterDisconnectReplyActive(simulator.isMeterDisconnectReplyActive() && (!simulator.isMeterDisconnectReplyStopping()));
        
        response.setSuccessful(true);
        return response;
    }
}
