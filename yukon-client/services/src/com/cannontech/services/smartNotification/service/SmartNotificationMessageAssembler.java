package com.cannontech.services.smartNotification.service;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.worker.SupervisedQueueProcessor;

/**
 * Builds smart notification messages based on event info and user preferences.
 */
public class SmartNotificationMessageAssembler implements SupervisedQueueProcessor {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationMessageAssembler.class);
    private static final String assemblerQueue = JmsApiDirectory.SMART_NOTIFICATION_ASSEMBLER.getQueue().getName();
    private JmsTemplate jmsTemplate;
    
    public SmartNotificationMessageAssembler(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }
    
    @Override
    public void start() {
        //Spin up a processor in a new thread. 
        //1. Pull messages from the assemblerQueue  
        //2. Compare with user prefs to figure out who to notify, how to notify
        //3. Build up actual messages
        //4. Send out messages via Notification service
    }
    
    @Override
    public void stop() {
        //Flag the processor to stop when it has finished processing the current message
    }
}
