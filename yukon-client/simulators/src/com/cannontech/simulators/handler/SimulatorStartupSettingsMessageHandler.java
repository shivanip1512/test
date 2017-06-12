package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsRequest;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsStatusRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorStartupSettingsResponse;
import com.cannontech.simulators.startup.service.SimulatorStartupSettingsService;

/**
 * Handles SimulatorStartupSettings messages for each of the meter simulators.
 */
public class SimulatorStartupSettingsMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(SimulatorStartupSettingsMessageHandler.class);
    @Autowired private SimulatorStartupSettingsService simulatorStartupSettingsService;

    public SimulatorStartupSettingsMessageHandler() {
        super(SimulatorType.SIMULATOR_STARTUP);
    }
    
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof SimulatorStartupSettingsRequest) {
                SimulatorStartupSettingsRequest request = (SimulatorStartupSettingsRequest) simulatorRequest;
                simulatorStartupSettingsService.uploadSimulatorStartupSettingsToDb(request.getRunOnStartup(), request.getUploadType());
                return new SimulatorStartupSettingsResponse(true);
            } else if (simulatorRequest instanceof SimulatorStartupSettingsStatusRequest) {
                SimulatorStartupSettingsStatusRequest request = (SimulatorStartupSettingsStatusRequest) simulatorRequest;
                boolean runOnStartup = simulatorStartupSettingsService.getRunOnStartup(request.getDownloadType());
                return new SimulatorStartupSettingsResponse(true, runOnStartup);
            } else {
                SimulatorStartupSettingsResponse response = new SimulatorStartupSettingsResponse(false);
                response.setException(new IllegalArgumentException("Bad simualtor request sent to SimulatorStartupSettingsMessageHandler."));
                return response;
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest + " with request type: " + simulatorRequest.getRequestType());
            if (e instanceof IllegalArgumentException) {
                SimulatorStartupSettingsResponse response = new SimulatorStartupSettingsResponse(false);
                response.setException(e);
                return response;
            }
            throw e;
        }
    }
}
