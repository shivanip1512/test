package com.cannontech.web.stars.gateway.listener;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.NotFoundException;

/**
 * This listener receives gateway data messages from service manager and caches them in the web server.
 */
public class GatewayDataTopicListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataTopicListener.class);
    
    @Autowired private RfnGatewayDataCache cache;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof GatewayDataResponse) {
                    GatewayDataResponse gatewayDataMessage = (GatewayDataResponse) object;
                    handleMessage(gatewayDataMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract GatewayDataResponse from message", e);
            }
        }
    }
    
    private void handleMessage(GatewayDataResponse message) {
        RfnIdentifier rfnIdentifier = message.getRfnIdentifier();
        try {
            RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            log.debug("Handling gateway data message: " + message);
            cache.put(rfnDevice.getPaoIdentifier(), new RfnGatewayData(message));
        } catch (NotFoundException e) {
            log.info("Unable to add gateway data to cache. Device lookup failed for " + rfnIdentifier);
        }
    }
}
