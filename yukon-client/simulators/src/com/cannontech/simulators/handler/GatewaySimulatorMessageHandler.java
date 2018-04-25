package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
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

/**
 * Handles setup of the RFN gateway simulator, which receives and sends messages related to gateways, which would
 * normally be handled by Network Manager.
 */
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
        if ((!simulator.isAutoDataReplyActive() || simulator.isAutoDataReplyStopping()) && (dataSettings != null)) {
            boolean success = simulator.startAutoDataReply(dataSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedUpdateReplySettings updateSettings = request.getUpdateSettings();
        if ((!simulator.isAutoUpdateReplyActive() || simulator.isAutoUpdateReplyStopping()) && (updateSettings != null)) {
            boolean success = simulator.startAutoUpdateReply(updateSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedCertificateReplySettings certificateSettings = request.getCertificateSettings();
        if ((!simulator.isAutoCertificateUpgradeReplyActive() || simulator.isAutoCertificateUpgradeReplyStopping()) && (certificateSettings != null)) {
            boolean success = simulator.startAutoCertificateReply(certificateSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedFirmwareReplySettings firmwareSettings = request.getFirmwareSettings();
        if ((!simulator.isAutoFirmwareReplyActive() || simulator.isAutoFirmwareReplyStopping()) && (firmwareSettings != null)) {
            boolean success = simulator.startAutoFirmwareReply(firmwareSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        SimulatedFirmwareVersionReplySettings firmwareVersionSettings = request.getFirmwareVersionSettings();
        if ((!simulator.isAutoFirmwareVersionReplyActive() || simulator.isAutoFirmwareVersionReplyStopping()) && (firmwareVersionSettings != null)) {
            boolean success = simulator.startAutoFirmwareVersionReply(firmwareVersionSettings);
            if (!success) {
                totalSuccess = false;
            }
        }
        
        return totalSuccess;
    }
    
    private GatewaySimulatorStatusResponse getStatus() {
        GatewaySimulatorStatusResponse response = new GatewaySimulatorStatusResponse();
        
        response.setDataSettings(simulator.getGatewayDataSettings());
        response.setDataReplyActive(simulator.isAutoDataReplyActive() && (!simulator.isAutoDataReplyStopping()));
        
        response.setCertificateSettings(simulator.getCertificateSettings());
        response.setCertificateReplyActive(simulator.isAutoCertificateUpgradeReplyActive() && (!simulator.isAutoCertificateUpgradeReplyStopping()));
        
        response.setUpdateSettings(simulator.getGatewayUpdateSettings());
        response.setUpdateReplyActive(simulator.isAutoUpdateReplyActive() && (!simulator.isAutoUpdateReplyStopping()));
        
        response.setFirmwareSettings(simulator.getFirmwareSettings());
        response.setFirmwareReplyActive(simulator.isAutoFirmwareReplyActive() && (!simulator.isAutoFirmwareReplyStopping()));
        
        response.setFirmwareVersionSettings(simulator.getFirmwareVersionSettings());
        response.setFirmwareVersionReplyActive(simulator.isAutoFirmwareVersionReplyActive() && (!simulator.isAutoFirmwareVersionReplyStopping()));
        
        response.setSuccessful(true);
        return response;
    }
}
