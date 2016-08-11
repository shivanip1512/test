package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.simulation.service.DataStreamingSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.DataStreamingSimulatorStatusRequest;
import com.cannontech.simulators.message.request.ModifyDataStreamingSimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.DataStreamingSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class DataStreamingSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(DataStreamingSimulatorMessageHandler.class);
    
    @Autowired private DataStreamingSimulatorService simulator;
    
    public DataStreamingSimulatorMessageHandler() {
        super(SimulatorType.DATA_STREAMING);
    }
    
    @Override
    public SimulatorResponse handle(SimulatorRequest request) {
        try {
            if (request instanceof DataStreamingSimulatorStatusRequest) {
                return getStatus();
            } else if (request instanceof ModifyDataStreamingSimulatorRequest) {
                ModifyDataStreamingSimulatorRequest dsRequest = (ModifyDataStreamingSimulatorRequest) request;
                return handleModifyRequest(dsRequest);
            } else {
                throw new IllegalArgumentException("Unsupported request type received: " + request.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + request);
            throw e;
        }
    }
    
    private DataStreamingSimulatorStatusResponse getStatus() {
        DataStreamingSimulatorStatusResponse response = new DataStreamingSimulatorStatusResponse();
        response.setRunning(simulator.isRunning());
        return response;
    }
    
    private SimulatorResponse handleModifyRequest(ModifyDataStreamingSimulatorRequest request) {
        if (request.isStopSimulator()) {
            simulator.stop();
            SimulatorResponse response = new SimulatorResponseBase();
            response.setSuccessful(true);
            return response;
        } else {
            boolean success = startSimulator(request);
            return new SimulatorResponseBase(success);
        }
    }
    
    private boolean startSimulator(ModifyDataStreamingSimulatorRequest request) {
        simulator.setSettings(request.getSettings());
        return simulator.start();
    }
    
}
