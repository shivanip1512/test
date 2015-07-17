package com.cannontech.common.rfn.service;

import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.model.SimulatedGatewayDataSettings;

/**
 * This is a development testing service that is intended to act as a replacement for Network Manager
 * in handling RF Gateway messaging.
 */
public interface RfnGatewaySimulatorService {
    
    /**
     * The simulator begins automatically processing GatewayDataRequests and sending GatewayDataResponses.
     * @param settings Configuration that determines the type of response sent. If null, default settings will be used.
     * @return True if automatic data reply was started, and false if automatic data reply was already running or in the
     * process of stopping.
     */
    boolean startAutoDataReply(SimulatedGatewayDataSettings settings);
    
    /**
     * The simulator stops automatically processing GatewayDataRequests.
     */
    void stopAutoDataReply(); 
    
    /**
     * The simulator begins automatically processing RfnGatewayUpgradeRequests and sending RfnGatewayUpgradeResponses.
     * @param settings Configuration that determines the type of response sent. If null, default settings will be used.
     * @return True if automatic upgrade reply was started, and false if automatic upgrade reply was already running or 
     * in the process of stopping.
     */
    boolean startAutoCertificateReply(SimulatedCertificateReplySettings settings);
    
    /**
     * The simulator stops automatically processing RfnGatewayUpgradeRequests
     */
    void stopAutoCertificateReply();
    
    /**
     * Send an unsolicited gateway data message.
     */
    void sendGatewayDataResponse(GatewayDataResponse response);
    
    /**
     * Send an unsolicited gateway archive message (which will create a new gateway device in Yukon.
     */
    void sendGatewayArchiveRequest(String name, String serial, boolean isGateway2);
    
}
