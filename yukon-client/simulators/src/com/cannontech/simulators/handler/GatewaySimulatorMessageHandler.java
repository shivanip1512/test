package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.GatewaySimulatorStatusRequest;
import com.cannontech.simulators.message.request.ModifyGatewaySimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.GatewaySimulatorStatusResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class GatewaySimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(GatewaySimulatorMessageHandler.class);
    @Autowired private RfnGatewaySimulatorService simulator;
    
    public GatewaySimulatorMessageHandler() {
        super(SimulatorType.GATEWAY);
    }
    
    @Override
    public SimulatorResponse handle(SimulatorRequest request) {
        try {
            if (request instanceof ModifyGatewaySimulatorRequest) {
                ModifyGatewaySimulatorRequest gatewayRequest = (ModifyGatewaySimulatorRequest) request;
                // Stop any gateway simulators with a stop request
                stopSimulators(gatewayRequest);
                // Start any gateway simulators that have non-null start settings in the request
                boolean success = startSimulators(gatewayRequest);
                return new SimulatorResponseBase(success);
            } else if (request instanceof GatewaySimulatorStatusRequest) {
                return getStatus();
            } else {
                throw new IllegalArgumentException("Unsupported request type received: " + request.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + request);
            throw e;
        }
    }
    
    private void stopSimulators(ModifyGatewaySimulatorRequest request) {
        if (request.isStopDataReply()) {
            simulator.stopAutoDataReply();
        }
        if (request.isStopCertificateReply()) {
            simulator.stopAutoCertificateReply();
        }
        if (request.isStopUpdateReply()) {
            simulator.stopAutoUpdateReply();
        }
        if (request.isStopFirmwareReply()) {
            simulator.stopAutoFirmwareReply();
        }
        if (request.isStopFirmwareVersionReply()) {
            simulator.stopAutoFirmwareVersionReply();
        }
    }
    
    private boolean startSimulators(ModifyGatewaySimulatorRequest request) {
        boolean totalSuccess = true;
        
        SimulatedGatewayDataSettings dataSettings = request.getDataSettings();
        if (dataSettings != null) {
            boolean success = simulator.startAutoDataReply(dataSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedUpdateReplySettings updateSettings = request.getUpdateSettings();
        if (updateSettings != null) {
            boolean success = simulator.startAutoUpdateReply(updateSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedCertificateReplySettings certificateSettings = request.getCertificateSettings();
        if (certificateSettings != null) {
            boolean success = simulator.startAutoCertificateReply(certificateSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedFirmwareReplySettings firmwareSettings = request.getFirmwareSettings();
        if (firmwareSettings != null) {
            boolean success = simulator.startAutoFirmwareReply(firmwareSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedFirmwareVersionReplySettings firmwareVersionSettings = request.getFirmwareVersionSettings();
        if (firmwareVersionSettings != null) {
            boolean success = simulator.startAutoFirmwareVersionReply(firmwareVersionSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        return totalSuccess;
    }
    
    private GatewaySimulatorStatusResponse getStatus() {
        GatewaySimulatorStatusResponse response = new GatewaySimulatorStatusResponse();
        
        if (simulator.isAutoDataReplyActive()) {
            response.setDataSettings(simulator.getGatewayDataSettings());
        }
        
        if (simulator.isAutoCertificateUpgradeReplyActive()) {
            response.setCertificateSettings(simulator.getCertificateSettings());
        }
        
        if (simulator.isAutoUpdateReplyActive()) {
            response.setUpdateSettings(simulator.getGatewayUpdateSettings());
        }
        
        if (simulator.isAutoFirmwareReplyActive()) {
            response.setFirmwareSettings(simulator.getFirmwareSettings());
        }
        
        if (simulator.isAutoFirmwareVersionReplyActive()) {
            response.setFirmwareVersionSettings(simulator.getFirmwareVersionSettings());
        }
        
        return response;
    }
}
