package com.cannontech.common.smartNotification.service.impl;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
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
    }
    
    @Override
    public void sendEvent(SmartNotificationEvent event) {
        log.debug("Sending Smart Notification event");
        log.trace(event);
        jmsTemplate.convertAndSend(eventQueue, event);
    }
}
