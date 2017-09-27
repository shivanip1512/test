package com.cannontech.services.smartNotification.service;

import static com.cannontech.common.smartNotification.model.SmartNotificationFrequency.COALESCING;
import static com.cannontech.common.smartNotification.model.SmartNotificationFrequency.DAILY_DIGEST;
import static com.cannontech.common.smartNotification.model.SmartNotificationFrequency.IMMEDIATE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.services.smartNotification.service.impl.SmartNotificationDeciderServiceImpl.WaitTime;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public abstract class SmartNotificationDecider implements MessageListener {

    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private SmartNotificationDeciderService deciderService;
    @Autowired protected SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired protected IDatabaseCache cache;
    @Autowired private ScheduledExecutor scheduledExecutor;
    
    protected SmartNotificationEventType eventType;
    
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDecider.class);
    private WaitTime waitTime;

    public boolean isScheduledToRunInTheFuture(){
       return waitTime != null && waitTime.getRunTime().isAfterNow();
    }
        
    @PostConstruct
    private void processOnStartup() {
        logInfo("Starting Smart Notification Event Decider");        
        List<SmartNotificationEvent> events = eventDao.getUnprocessedEvents(eventType);
        logInfo("Processing " + events.size() + " Smart Notification Events");
        if (!events.isEmpty()) {
            ProcessorResult result = process(Instant.now(), events);
            deciderService.send(result);
        }
        scheduleDailyDigest();
    }
    
    
    private void scheduleDailyDigest() {
        // run every hour
        logInfo("Scheduling Daily Digest. First run in 5 minutes.");
        scheduledExecutor.scheduleWithFixedDelay(() -> {

            //figure out if it is time to send digest?
            
            // get events for date range
            List<SmartNotificationEvent> events = new ArrayList<>();
            
            log.info(eventType + " Processing DAILY_DIGEST " + events.size() + " Events");
            List<SmartNotificationEvent> validEvents = validate(events);
            SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> dailyDigest =
                getSubscriptionsForEvents(validEvents, DAILY_DIGEST);

            ProcessorResult result = new ProcessorResult(this);
            result.addMessageParameters(MessageParametersHelper.getMessageParameters(eventType, dailyDigest));
            deciderService.send(result);
            
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    private void logDebug(String text) {
        log.debug(deciderService.getLogMsg(text, this));
    }
    
    private void logInfo(String text) {
        log.info(deciderService.getLogMsg(text, this));
    }

    /**
     *  Returns events that can be processed. Marks events that can't be processed.
     */
    private EventsByFrequency validateEvents(List<SmartNotificationEvent> events, Instant now) {
        if (!events.isEmpty()) {
            List<SmartNotificationEvent> validEvents = validate(events);
            if(validEvents.isEmpty()){
                eventDao.markEventsAsProcessed(events, now, COALESCING, IMMEDIATE);
                logDebug(events.size() + " events invalid because monitors or devices do not exist. Marked events as processed.");
                //no events to process
                return null;
            } else {
                SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> coalescing =
                    getSubscriptionsForEvents(
                        validEvents.stream().filter(e -> e.getGroupProcessTime() == null).collect(Collectors.toList()),
                        COALESCING);
                SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> immediate =
                    getSubscriptionsForEvents(
                        validEvents.stream().filter(e -> e.getImmediateProcessTime() == null).collect(
                            Collectors.toList()),
                        IMMEDIATE);
                
                // no subscriptions for received events.
                List<SmartNotificationEvent> eventsWithoutSubscriptions = new ArrayList<>(events); 
                eventsWithoutSubscriptions.removeAll(coalescing.values());
                eventsWithoutSubscriptions.removeAll(immediate.values());
                if(!eventsWithoutSubscriptions.isEmpty()){
                    eventDao.markEventsAsProcessed(eventsWithoutSubscriptions, now, COALESCING, IMMEDIATE);
                    logDebug(eventsWithoutSubscriptions.size() + " events that have no subscriptions or already processed.");
                }
                if (coalescing.isEmpty() && immediate.isEmpty()) {
                    //no events to process
                    return null;
                } else {
                    EventsByFrequency eventsByFrequency = new EventsByFrequency();
                    eventsByFrequency.coalescing = coalescing;
                    eventsByFrequency.immediate = immediate;
                    return eventsByFrequency;
                }
            } 
        }
        return null;
     }
     
     private final class EventsByFrequency{
         SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> coalescing;
         SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> immediate;
     }
     
    private void processCoalescingEvents(EventsByFrequency eventsByFrequency,
            Instant now, ProcessorResult result) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> coalescing = eventsByFrequency.coalescing;
        if (!coalescing.isEmpty()) {
            if (isScheduledToRunInTheFuture()) {
                result.setReschedule(false);
                log.debug(eventType + " Unable to process " + Sets.newHashSet(coalescing.values()).size()
                    + " events. The events will be processed at " + waitTime);
            } else {
                if (waitTime == null && deciderService.getFirstInterval() != 0) {
                    // user wants to wait before getting his first notification
                    result.setReschedule(true);
                } else {
                    Set<SmartNotificationEvent> events = Sets.newHashSet(coalescing.values());
                    eventDao.markEventsAsProcessed(Lists.newArrayList(events), now, COALESCING);
                    result.addMessageParameters(MessageParametersHelper.getMessageParameters(eventType, coalescing));
                    result.setReschedule(true);
                    
                    Set<SmartNotificationEvent> immediateEvents = Sets.newHashSet(coalescing.values());
                    immediateEvents.removeAll(eventsByFrequency.immediate.values());
                    eventDao.markEventsAsProcessed(Lists.newArrayList(immediateEvents), now, IMMEDIATE);
                }
            }
        }
    }
   
    private void processImmediateEvents(EventsByFrequency eventsByFrequency,
            Instant now, ProcessorResult result) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> immediate = eventsByFrequency.immediate;

        if (!immediate.isEmpty()) {
            Set<SmartNotificationEvent> events = Sets.newHashSet(immediate.values());
            eventDao.markEventsAsProcessed(Lists.newArrayList(events), now, IMMEDIATE);
            result.addMessageParameters(MessageParametersHelper.getMessageParameters(eventType, immediate));
            
            Set<SmartNotificationEvent> coalescingEvents = Sets.newHashSet(immediate.values());
            coalescingEvents.removeAll(eventsByFrequency.coalescing.values());
            eventDao.markEventsAsProcessed(Lists.newArrayList(coalescingEvents), now, COALESCING);
        }
    }
    /**
     * Processed events and returns message parameters.
     */
    public ProcessorResult process(Instant now, List<SmartNotificationEvent> events) {
        ProcessorResult result = new ProcessorResult(this);

        EventsByFrequency eventsByFrequency = validateEvents(events, now);
        if(eventsByFrequency != null){
            processCoalescingEvents(eventsByFrequency, now, result);
            processImmediateEvents(eventsByFrequency, now, result);
        }
        return result;

    }
        
    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        Serializable object;
        try {
            if (message instanceof ObjectMessage) {
                object = objMessage.getObject();
                if (object instanceof SmartNotificationEventMulti) {
                    SmartNotificationEventMulti event = (SmartNotificationEventMulti) object;
                    log.debug(eventType + " Processing message=" + event);
                    ProcessorResult result = process(Instant.now(), event.getEvents());
                    deciderService.send(result);
                }
            }
        } catch (JMSException e) {
            log.error("Unable to extract message", e);
        } catch (Exception e) {
            log.error("Unable to process message", e);
        }
    }
 
    /**
     * Returns events that can be processed. 
     */
    public abstract List<SmartNotificationEvent> validate(List<SmartNotificationEvent> events);

    /**
     * Returns map of subscriptions to events. 
     */
    public abstract SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> events, SmartNotificationFrequency frequency);
    
    /**
     * Returns time the events will be processed next. Return null if this decider is not scheduled to process events.
     */
    public WaitTime getWaitTime() {
        return waitTime;
    }

    /**
     * Sets next process time.
     */
    public void setWaitTime(WaitTime waitTime) {
        this.waitTime = waitTime;
    }

    public SmartNotificationEventType getEventType() {
        return eventType;
    }
    

    public final class ProcessorResult {
        private SmartNotificationDecider decider;
        private List<SmartNotificationMessageParameters> messageParameters = new ArrayList<>();
        private boolean reschedule = false;
        private WaitTime nextRun;

        public ProcessorResult(SmartNotificationDecider decider) {
            this.decider = decider;
        }

        public SmartNotificationDecider getDecider() {
            return decider;
        }

        public WaitTime getNextRun() {
            return nextRun;
        }

        public List<SmartNotificationMessageParameters> getMessageParameters() {
            return messageParameters;
        }

        public void addMessageParameters(List<SmartNotificationMessageParameters> messageParameters) {
            this.messageParameters.addAll(messageParameters);
        }

        public boolean isReschedule() {
            return reschedule;
        }

        public void setReschedule(boolean reschedule) {
            this.reschedule = reschedule;
            Instant now = Instant.now();
            if (reschedule) {
                if (waitTime == null) {
                    if (deciderService.getFirstInterval() == 0) {
                        nextRun = WaitTime.getFirst(now).getNext(now);
                    } else {
                        nextRun = WaitTime.getFirst(now);
                    }
                } else {
                    nextRun = decider.getWaitTime().getNext(Instant.now());
                }
            }
        }

        public boolean hasMessages() {
            return !messageParameters.isEmpty();
        }
    }

}
