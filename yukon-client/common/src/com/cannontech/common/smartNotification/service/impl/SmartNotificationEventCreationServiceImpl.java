package com.cannontech.common.smartNotification.service.impl;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;

public class SmartNotificationEventCreationServiceImpl implements SmartNotificationEventCreationService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEventCreationServiceImpl.class);
    private static final String eventQueue = JmsApiDirectory.SMART_NOTIFICATION_EVENT.getQueue().getName();
    @Autowired private ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
    }
    
    @Override
    public void sendEvents(SmartNotificationEventMulti multi) {
        log.debug("Sending Smart Notification event multi");
        if (log.isTraceEnabled()) {
            for(SmartNotificationEvent event : multi.getEvents()) {
                log.trace(event);
            }
        }
        jmsTemplate.convertAndSend(eventQueue, multi);
    }
}
