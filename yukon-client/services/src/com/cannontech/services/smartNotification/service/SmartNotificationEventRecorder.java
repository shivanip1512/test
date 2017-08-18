package com.cannontech.services.smartNotification.service;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.worker.SupervisedQueueProcessor;

/**
 * These workers are supervised by the SmartNotificationEventListener. They are interchangeable and can be created or
 * destroyed depending on the number of events in the queue. While running, they continuously pull events from the
 * queue and push them into the database.
 */
public class SmartNotificationEventRecorder implements SupervisedQueueProcessor {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEventRecorder.class);
    private static final String recorderQueue = JmsApiDirectory.SMART_NOTIFICATION_RECORDER.getQueue().getName();
    private JmsTemplate jmsTemplate;
    private SmartNotificationEventDao smartNotificationEventDao;
    
    public SmartNotificationEventRecorder(ConnectionFactory connectionFactory, SmartNotificationEventDao smartNotificationEventDao) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        this.smartNotificationEventDao = smartNotificationEventDao;
        start();
    }
    
    /**
     * Spin up a processor in a new thread. It pulls messages from the recorderQueue and 
     * pushes them into the SmartNotificationEventDao
     */
    @Override
    public void start() {
        
    }
    
    /**
     * Flag the worker to stop when it has finished processing the current message
     */
    @Override
    public void stop() {
        
    }
    
}
