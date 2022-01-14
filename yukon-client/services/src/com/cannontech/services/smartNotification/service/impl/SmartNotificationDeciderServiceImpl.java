package com.cannontech.services.smartNotification.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider.ProcessorResult;
import com.cannontech.services.smartNotification.service.SmartNotificationDeciderService;
import com.google.common.collect.Multimap;

/**
 * This class is responsible for scheduling checks for unprocessed events and sending smart notification
 * message information to assemblers.
 */
public class SmartNotificationDeciderServiceImpl implements SmartNotificationDeciderService, MessageListener {
        
    private static Logger snLogger = YukonLogManager.getSmartNotificationsLogger(SmartNotificationDeciderServiceImpl.class);
    
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired protected SmartNotificationEventDao eventDao;
    private boolean isScheduled;
    private Map<SmartNotificationEventType, SmartNotificationDecider> deciders;

    private YukonJmsTemplate jmsTemplate;
    private final Executor executor = Executors.newCachedThreadPool();

    @Autowired
    private SmartNotificationDeciderServiceImpl(List<SmartNotificationDecider> deciders){
        this.deciders = deciders.stream()
                .collect(Collectors.toMap(SmartNotificationDecider::getEventType, Function.identity()));
    }
    
    @PostConstruct
    private void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_MESSAGE_PARAMETERS);
        scheduledExecutor.execute(this::processOnStartup);
    }

    /**
     * On the startup attempts to find and send unprocessed events
     */
    private void processOnStartup() {
        // grouped subscriptions        
        processOnStartup(eventDao.getUnprocessedEvents(SmartNotificationFrequency.COALESCING), SmartNotificationFrequency.COALESCING);
        // immediate subscriptions
        processOnStartup(eventDao.getUnprocessedEvents(SmartNotificationFrequency.IMMEDIATE), SmartNotificationFrequency.IMMEDIATE);
    }

    private void processOnStartup(Multimap<SmartNotificationEventType, SmartNotificationEvent> unprocessed,
            SmartNotificationFrequency frequency) {
        deciders.forEach((type, decider) -> {
            List<SmartNotificationEvent> events = new ArrayList<>(unprocessed.get(type));
            if (!events.isEmpty()) {
                send(decider.processOnStartup(events, frequency));
            }
        });
    }
    
    /**
     * Create as schedule that runs every minute 
     */
    private void scheduleIntervalProcessing() {
        if(isScheduled) {
            return;
        }
        
        isScheduled = true;
       
        snLogger.info("Scheduling interval processing");
        
        scheduledExecutor.scheduleAtFixedRate(() -> {
            deciders.values().forEach(decider -> {
                // for each decider figure out what events need to be send now and send those events
                decider.processOnInterval().forEach(result -> send(result));
            });
        }, 0, 1, TimeUnit.MINUTES);
        
    }

    /**
     * Takes result received from decider puts the necessary info on a queue for email to be generated and sent
     */
    private void send(ProcessorResult result) {
        if (result.hasMessages()) {
            executor.execute(() -> {
                try {
                    putMessagesOnAssemblerQueue(result);
                } catch (Exception e) {
                    snLogger.error("Error sending smart notification message parameters", e);
                }
            });
        } 
    }
    
    /**
     * Puts the necessary info on a queue for email to be generated and sent
     */
    private void putMessagesOnAssemblerQueue(ProcessorResult result) {
        if (result.hasMessages()) {
            int interval = result.getInterval();
            List<SmartNotificationMessageParameters> messages = result.getMessageParameters();
            SmartNotificationMessageParametersMulti msg = new SmartNotificationMessageParametersMulti(messages, interval, false);
            snLogger.info("Put on assembler queue:{}", msg.loggingString(snLogger.getLevel()));
            jmsTemplate.convertAndSend(msg);
        }
    }

    @Override
    public void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval,
            boolean sendAllInOneEmail, String digestTime) {
        if (!messages.isEmpty()) {
            SmartNotificationMessageParametersMulti msg = new SmartNotificationMessageParametersMulti(messages, interval,
                    sendAllInOneEmail);
            snLogger.info("Daily Digest Total:{} Digest Time:{} One email:{} Put on assembler queue:{}", messages.size(),
                    digestTime, sendAllInOneEmail, msg.loggingString(snLogger.getLevel()));
            jmsTemplate.convertAndSend(msg);
        }
    }
    
    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        Serializable object;
        try {
            if (message instanceof ObjectMessage) {
                object = objMessage.getObject();
                if (object instanceof SmartNotificationEventMulti) {
                    
                    // create new schedule for processing of coalesced events
                    scheduleIntervalProcessing();
                    
                    SmartNotificationEventMulti event = (SmartNotificationEventMulti) object;
                    //For received event find decider
                    SmartNotificationDecider decider = deciders.get(event.getEventType());
                    if (decider != null) {
                        //Find and process immediate subscriptions
                        ProcessorResult immediate = decider.processImmediateSubscriptions(event.getEvents());
                        //Find and process grouped subscriptions
                        ProcessorResult grouped = decider.processGroupedSubscriptions(event.getEvents());
                        //Log event and processing result
                        
                        snLogger.info("Received event:{} Immediate result:{}  Grouped result:{}", event, immediate.loggingString(snLogger.getLevel()), grouped.loggingString(snLogger.getLevel()));
                        
                        //Send results for immediate subscriptions
                        send(immediate);
                        //Send results for grouped subscriptions in interval is 0
                        send(grouped);
                    } else {
                        snLogger.error("Decider for {} doesn't exist", event.getEventType());
                    }
                }
            }
        } catch (Exception e) {
            snLogger.error("Unable to process message", e);
        }
    } 
}
