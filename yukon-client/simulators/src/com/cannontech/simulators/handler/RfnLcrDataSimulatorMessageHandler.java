package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.RfnLcrAllDeviceSimulatorStartRequest;
import com.cannontech.simulators.message.request.RfnLcrAllDeviceSimulatorStopRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorByRangeStartRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorByRangeStopRequest;
import com.cannontech.simulators.message.request.RfnLcrSimulatorStatusRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.RfnLcrSimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class RfnLcrDataSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnLcrDataSimulatorMessageHandler.class);
    @Autowired private RfnLcrDataSimulatorService rfnLcrDataSimulatorService;

    public RfnLcrDataSimulatorMessageHandler() {
        super(SimulatorType.RFN_LCR);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            
            if (simulatorRequest instanceof RfnLcrSimulatorByRangeStartRequest) {
                RfnLcrSimulatorByRangeStartRequest request = (RfnLcrSimulatorByRangeStartRequest) simulatorRequest;
                rfnLcrDataSimulatorService.sendMessagesByRange(request.getSettings());
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnLcrSimulatorByRangeStopRequest) {
                rfnLcrDataSimulatorService.stopRangeSimulator();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnLcrSimulatorStatusRequest) {
                SimulatorSettings settings = rfnLcrDataSimulatorService.getCurrentSettings();
                RfnDataSimulatorStatus allDevicesStatus = rfnLcrDataSimulatorService.getAllDevicesStatus();
                RfnDataSimulatorStatus statusByRange = rfnLcrDataSimulatorService.getStatusByRange();
                return new RfnLcrSimulatorStatusResponse(allDevicesStatus, statusByRange, settings);
            } else if (simulatorRequest instanceof RfnLcrAllDeviceSimulatorStartRequest) {
                RfnLcrAllDeviceSimulatorStartRequest request = (RfnLcrAllDeviceSimulatorStartRequest) simulatorRequest;
                rfnLcrDataSimulatorService.sendMessagesToAllDevices(request.getSettings());
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof RfnLcrAllDeviceSimulatorStopRequest) {
                rfnLcrDataSimulatorService.stopAllDeviceSimulator();
                return new SimulatorResponseBase(true);
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
