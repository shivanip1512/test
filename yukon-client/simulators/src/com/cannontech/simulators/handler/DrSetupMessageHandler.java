package com.cannontech.simulators.handler;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.development.service.DemandResponseSetupService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.DrSetupSimulatorRequest;
import com.cannontech.simulators.message.request.DrSetupSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class DrSetupMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(DrSetupMessageHandler.class);
    @Autowired private DemandResponseSetupService drSetupService;
    @Autowired @Qualifier("longRunning") private Executor executor;

    public DrSetupMessageHandler() {
        super(SimulatorType.DEMAND_RESPONSE_SETUP);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof DrSetupSimulatorRequest) {
                DrSetupSimulatorRequest request = (DrSetupSimulatorRequest) simulatorRequest;
                if (!drSetupService.isRunning()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                drSetupService.executeSetup(request.getDemandResponseSetup());
                            } catch (Exception e) {
                                log.error("Demand response setup failed", e);
                            }
                        }
                    };
                    executor.execute(thread);
                    return new SimulatorResponseBase(true);
                }
                return new SimulatorResponseBase(false);
            } else if (simulatorRequest instanceof DrSetupSimulatorStatusRequest) {
                return new SimulatorResponseBase(drSetupService.isRunning());
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