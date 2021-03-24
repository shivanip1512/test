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


/**TODO
 * Figure out how to to prove that this is sending correct number of emails to correct people on correct intervals with multiple subscriptions. Use data monitors and infra. warnings - requires simulator fix.
 */

/**Additional TODO
 * 1. Test with intervals that do not start with 0 (currently tested with SMART_NOTIFICATION_INTERVALS: 0,1,2)
 * 2. Test with SMART_NOTIFICATION_INTERVALS: 0,1,3,5,15,30
 * 3. Daily Digest, doesn't log correctly and needs to be re-tested 
 * 4. Test all smart notification types
 * 5. Cache asset import per import result type, not per smart notification type
 * 6. Fix Smart notification simulator to send to all types. Re-test the simulator - currently tested only with device data monitors
 * 7. Create confluence page describing how to use simulator, and how logging works
 * 8. Document for QA what should be tested
 * 9. Fix unit test if applicable
 * 10. Test - Unsubscribe from subscription while events are coalescing 
 * 11. Test - Processing on start-up
 * 12. Change simulator to use only enabled monitors (send to all case)
 */

/**
 * This class is responsible for scheduling checks for unprocessed events and sending smart notification
 * message information to assemblers.
 */
public class SmartNotificationDeciderServiceImpl implements SmartNotificationDeciderService, MessageListener {
        
    private static Logger commsLogger = YukonLogManager.getCommsLogger();

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
        scheduledExecutor.execute(() -> {
            processOnStartup();
        });
    }

    /**
     * On the startup attempts to find and send unprocessed events
     */
    private void processOnStartup() {
        // grouped subscriptions        
        processOnStartup(eventDao.getUnprocessedEvents(true), SmartNotificationFrequency.COALESCING);
        // immediate subscriptions
        processOnStartup(eventDao.getUnprocessedEvents(false), SmartNotificationFrequency.IMMEDIATE);
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
       
        logInfo("Scheduling interval processing", this);
        
        scheduledExecutor.scheduleAtFixedRate(() -> {
            deciders.values().forEach(decider -> {
                // for each decider figure out what events need to be send now and send those events
                decider.processOnInterval().forEach(result -> send(result));
            });
        }, 0, 1, TimeUnit.MINUTES);
        
    }
    
    @Override
    public void logInfo(String text, Object obj) {
        commsLogger.info("[SN:{}] {}", obj.getClass().getSimpleName(), text);
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
                    commsLogger.error("Error sending smart notification message parameters", e);
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
            logInfo("Put on assembler queue: " + msg.loggingString(commsLogger.getLevel()), this);
            jmsTemplate.convertAndSend(msg);
        }
    }

    @Override
    public void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval,
            boolean sendAllInOneEmail, String digestTime) {
        if (!messages.isEmpty()) {
            SmartNotificationMessageParametersMulti msg = new SmartNotificationMessageParametersMulti(messages, interval,
                    sendAllInOneEmail);
            logInfo("Daily Digest Total:" + messages.size() + " Digest Time:" + digestTime
                    + " One email:" + sendAllInOneEmail + " Put on assembler queue: " + msg.loggingString(commsLogger.getLevel()), this);
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
                    // in there is no schedule to check every minute if there are additional messages to process create a
                    // schedule.
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
                        logInfo("Received event: " + event + " Immediate result:"
                                + immediate.loggingString(commsLogger.getLevel()) + " Grouped result:"
                                + grouped.loggingString(commsLogger.getLevel()), this);
                        //Send results from immediate subscriptions
                        send(immediate);
                        //Send results from grouped subscriptions (interval 0)
                        send(grouped);
                    } else {
                        commsLogger.error("Decider for {} doesn't exist", event.getEventType());
                    }
                }
            }
        } catch (Exception e) {
            commsLogger.error("Unable to process message", e);
        }
    } 
}
