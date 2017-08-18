package com.cannontech.services.smartNotification.service;

import static com.cannontech.common.smartNotification.model.SmartNotificationEventType.*;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.jms.api.JmsApi;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.worker.QueueProcessorSupervisor;
import com.cannontech.common.worker.SupervisedQueueProcessor;
import com.google.common.collect.ImmutableMap;


/**
 * This listener processes Smart Notification events sent from any part of Yukon that wants to send smart notifications.
 * The events are placed into a recorder queue and a decider queue.
 * 
 * The recorder queue is serviced by SmartNotificationEventRecorders, which push each event into the database. This
 * service is also responsible for supervising the recorders, and creating or destroying them depending on the number
 * of events on the recording queue.
 * 
 * The decider queue is serviced by SmartNotificationDeciders. There is one for each event type.
 */
public class SmartNotificationEventListener extends QueueProcessorSupervisor {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEventListener.class);//TODO: logging
    private static final String recorderQueue = JmsApiDirectory.SMART_NOTIFICATION_RECORDER.getQueue().getName();
    private static final Map<SmartNotificationEventType, JmsApi<?,?,?>> eventTypeToQueue = ImmutableMap.of(
        INFRASTRUCTURE_WARNING, JmsApiDirectory.SMART_NOTIFICATION_INFRASTRUCTURE_WARNINGS_DECIDER,
        DEVICE_DATA_MONITOR, JmsApiDirectory.SMART_NOTIFICATION_DEVICE_DATA_MONITOR_DECIDER
    );
    
    @Autowired @Qualifier("main") private ScheduledExecutorService executor;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private SmartNotificationEventDao smartNotificationEventDao;
    private JmsTemplate jmsTemplate;
    
    public SmartNotificationEventListener() {
        super(new WorkerSupervisorBuilder(recorderQueue));
    }
    
    @Override
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }
    
    /**
     * Put each event on the decider queue and the recorder queue.
     */
    //TODO: add xml wiring this as listener for yukon.notif.obj.smartNotifEvent.event
    void handle(SmartNotificationEvent event) {
        jmsTemplate.convertAndSend(recorderQueue, event);
        String deciderQueue = eventTypeToQueue.get(event.getType()).getQueue().getName();
        jmsTemplate.convertAndSend(deciderQueue, event);
    }
    
    @Override
    protected SupervisedQueueProcessor getNewWorker() {
        return new SmartNotificationEventRecorder(connectionFactory, smartNotificationEventDao);
    }
}
