package com.cannontech.messaging.service.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/** 
 * Prints out logging whenever a string is sent to this endpoint
 */
@Service
public class CloudConnectorPingListener {
    
    Logger log = (Logger) LogManager.getLogger(CloudConnectorPingListener.class);
    
    @Autowired JmsTemplate jmsTemplate;
    
    @JmsListener(destination = "com.eaton.eas.cloud.cloudconnector.PingRequest")
    public void receiveMessage(String message) {
        log.info("Ping message received: {}", message);
    }
}
