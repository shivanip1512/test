package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.simulation.SimulatedRfnDeviceDeletionSettings;
import com.cannontech.common.rfn.simulation.service.RfnDeviceDeletionSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.ModifyRfnDeviceDeletionSimulatorRequest;
import com.cannontech.simulators.message.request.RfnDeviceDeletionSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.RfnDeviceDeletionSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class RfnDeviceDeleteSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceDeleteSimulatorMessageHandler.class);
    @Autowired RfnDeviceDeletionSimulatorService simulator;

    public RfnDeviceDeleteSimulatorMessageHandler() {
        super(SimulatorType.RFN_DEVICE_DELETE);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest request) {
        try {
            if (request instanceof ModifyRfnDeviceDeletionSimulatorRequest) {
                ModifyRfnDeviceDeletionSimulatorRequest simulatorRequest = (ModifyRfnDeviceDeletionSimulatorRequest) request;
                stopSimulator(simulatorRequest);

                boolean success = startSimulator(simulatorRequest);
                return new SimulatorResponseBase(success);
            } else if (request instanceof RfnDeviceDeletionSimulatorStatusRequest) {
                return getStatus();
            } else {
                throw new IllegalArgumentException("Unsupported request type received: " + request.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + request);
            throw e;
        }
    }

    private void stopSimulator(ModifyRfnDeviceDeletionSimulatorRequest request) {
        if (request.isStopDeletionReply()) {
            simulator.stopRfnDeviceDeletionReply();
        }
    }

    private boolean startSimulator(ModifyRfnDeviceDeletionSimulatorRequest request) {
        boolean completeSuccess = true;
        SimulatedRfnDeviceDeletionSettings deletionSettings = request.getDeletionSettings();
        if ((!simulator.isRfnDeviceDeletionReplyActive() || simulator.isRfnDeviceDeletionReplyStopping())
                && (deletionSettings != null)) {
            boolean success = simulator.startRfnDeviceDeletionReply(deletionSettings);
            if (!success) {
                completeSuccess = false;
            }
        }
        return completeSuccess;
    }

    private RfnDeviceDeletionSimulatorStatusResponse getStatus() {
        RfnDeviceDeletionSimulatorStatusResponse response = new RfnDeviceDeletionSimulatorStatusResponse();

        response.setDeletionSettings(simulator.getDeletionSettings());
        response.setDeviceDeletionReplyActive(
                simulator.isRfnDeviceDeletionReplyActive() && (!simulator.isRfnDeviceDeletionReplyStopping()));

        response.setSuccessful(true);
        return response;
    }
}
