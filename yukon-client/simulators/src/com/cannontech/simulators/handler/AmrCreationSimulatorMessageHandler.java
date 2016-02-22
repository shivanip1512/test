package com.cannontech.simulators.handler;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.development.service.DevAmrCreationService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.AmrCreationSimulatorRequest;
import com.cannontech.simulators.message.request.AmrCreationSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class AmrCreationSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(AmrCreationSimulatorMessageHandler.class);
    @Autowired private DevAmrCreationService devAmrCreationService;
    @Autowired @Qualifier("longRunning") private Executor executor;

    public AmrCreationSimulatorMessageHandler() {
        super(SimulatorType.AMR_CREATION);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof AmrCreationSimulatorRequest) {
                AmrCreationSimulatorRequest request = (AmrCreationSimulatorRequest) simulatorRequest;
                if (!devAmrCreationService.isRunning()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                devAmrCreationService.executeSetup(request.getDevAmr());
                            } catch (Exception e) {
                                log.error("Asynchronous device creation failed", e);
                            }
                        }
                    };
                    executor.execute(thread);
                    return new SimulatorResponseBase(true);
                }
                return new SimulatorResponseBase(false);
            } else if (simulatorRequest instanceof AmrCreationSimulatorStatusRequest) {
                return new SimulatorResponseBase(devAmrCreationService.isRunning());
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
