package com.cannontech.simulators.handler;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.development.service.DevAMRCreationService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.AMRCreationSimulatorRequest;
import com.cannontech.simulators.message.request.AMRCreationSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class AMRCreationSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(AMRCreationSimulatorMessageHandler.class);
    @Autowired private DevAMRCreationService devAMRCreationService;
    @Autowired @Qualifier("longRunning") private Executor executor;

    public AMRCreationSimulatorMessageHandler() {
        super(SimulatorType.AMR_CREATION);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof AMRCreationSimulatorRequest) {
                AMRCreationSimulatorRequest request = (AMRCreationSimulatorRequest) simulatorRequest;
                if (!devAMRCreationService.isRunning()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                devAMRCreationService.executeSetup(request.getDevAMR());
                            } catch (Exception e) {
                                log.error("Asynchronous device creation failed", e);
                            }
                        }
                    };
                    executor.execute(thread);
                    return new SimulatorResponseBase(true);
                }
                return new SimulatorResponseBase(false);
            } else if (simulatorRequest instanceof AMRCreationSimulatorStatusRequest) {
                if (devAMRCreationService.isRunning()) {
                    return new SimulatorResponseBase(true);
                } else {
                    return new SimulatorResponseBase(false);
                }
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
