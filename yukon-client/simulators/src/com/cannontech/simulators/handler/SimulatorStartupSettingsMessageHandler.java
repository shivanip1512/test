package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
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
 * Handles SimulatorStartupSettings messages for retrieving and saving startup settings for
 * each of the meter simulators.
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
                simulatorStartupSettingsService.saveStartupSettings(request.isRunOnStartup(), request.getAffectedSimulator());
                return new SimulatorStartupSettingsResponse(true);
            } else if (simulatorRequest instanceof SimulatorStartupSettingsStatusRequest) {
                SimulatorStartupSettingsStatusRequest request = (SimulatorStartupSettingsStatusRequest) simulatorRequest;
                boolean runOnStartup = simulatorStartupSettingsService.isRunOnStartup(request.getAffectedSimulator());
                return new SimulatorStartupSettingsResponse(true, runOnStartup);
            } else {
                throw new IllegalArgumentException("Bad simualtor request sent to SimulatorStartupSettingsMessageHandler.");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                SimulatorStartupSettingsResponse response = new SimulatorStartupSettingsResponse(false);
                response.setException(e);
                return response;
            }
            throw e;
        }
    }
}
