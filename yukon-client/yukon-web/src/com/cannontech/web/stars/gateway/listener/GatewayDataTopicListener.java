package com.cannontech.web.stars.gateway.listener;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.NotFoundException;

/**
 * This listener receives gateway data messages and gateway certificate update responses from service manager.
 * Gateway data messages are cached in RfnGatewayDataCache.
 * Gateway upgrade responses are used to update the database on the state of the gateway certificate update.
 */
public class GatewayDataTopicListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataTopicListener.class);
    
    @Autowired private RfnGatewayDataCache cache;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof GatewayDataResponse) {
                    GatewayDataResponse gatewayDataMessage = (GatewayDataResponse) object;
                    handleDataMessage(gatewayDataMessage);
                } else if (object instanceof RfnGatewayUpgradeResponse) {
                    RfnGatewayUpgradeResponse gatewayUpgradeMessage = (RfnGatewayUpgradeResponse) object;
                    handleGatewayUpgradeMessage(gatewayUpgradeMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract GatewayDataResponse or RfnGatewayUpgradeResponse from message", e);
            }
        }
    }
    
    private void handleDataMessage(GatewayDataResponse message) {
        RfnIdentifier rfnIdentifier = message.getRfnIdentifier();
        try {
            RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            log.debug("Handling gateway data message: " + message);
            cache.put(rfnDevice.getPaoIdentifier(), new RfnGatewayData(message));
        } catch (NotFoundException e) {
            log.info("Unable to add gateway data to cache. Device lookup failed for " + rfnIdentifier);
        }
    }
    
    private void handleGatewayUpgradeMessage(RfnGatewayUpgradeResponse gatewayUpgradeMessage) {
        
        String certificateId = gatewayUpgradeMessage.getUpgradeId();
        int updateId = certificateUpdateDao.getLatestUpdateForCertificate(certificateId);
        
        RfnIdentifier rfnId = gatewayUpgradeMessage.getRfnIdentifier();
        int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
        
        RfnGatewayUpgradeResponseType responseType = gatewayUpgradeMessage.getResponseType();
        GatewayCertificateUpdateStatus status = GatewayCertificateUpdateStatus.of(responseType);
        
        certificateUpdateDao.updateEntry(updateId, paoId, status);
    }
}
