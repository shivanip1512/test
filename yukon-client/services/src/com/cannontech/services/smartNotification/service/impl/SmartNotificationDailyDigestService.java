package com.cannontech.services.smartNotification.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.services.smartNotification.service.MessageParametersHelper;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.cannontech.services.smartNotification.service.SmartNotificationDeciderService;
import com.google.common.collect.SetMultimap;

public class SmartNotificationDailyDigestService implements MessageListener {

    @Autowired protected SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired protected SmartNotificationEventDao eventDao;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private List<SmartNotificationDecider> deciders;
    @Autowired private SmartNotificationDeciderService deciderService;
    
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDailyDigestService.class);
   
    @PostConstruct
    private void scheduleDailyDigest() {
        log.info("Scheduling Daily Digest");
        int minutesToNextRun = 60 - DateTime.now().getMinuteOfHour();
        
        scheduledExecutor.scheduleWithFixedDelay(() -> doDailyDigest(DateTime.now()), minutesToNextRun, 60, TimeUnit.MINUTES);
    }
    
    private void doDailyDigest(DateTime now) {
        try {
            log.info("Running Daily Digest for "+ now.toString("MM-dd-yyyy HH:mm:ss"));
            String digestTime = now.withMinuteOfHour(0).toString("H:mm"); //Digest should always be on the hour
            log.debug("Digest time: " + digestTime);
            
            doDailyDigestGrouped(digestTime);
            doDailyDigestUngrouped(digestTime);
        } catch (Exception e) {
            log.error("Unexpected exception occurred while processing Smart Notification Daily Digest.", e);
        }
    }
    
    private void doDailyDigestGrouped(String digestTime) {
        List<SmartNotificationMessageParameters> allMessages = new ArrayList<>();
        SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> combinedSubscriptions = subscriptionDao.getDailyDigestGrouped(digestTime);
        for (SmartNotificationEventType type : combinedSubscriptions.keySet()) {
            List<SmartNotificationMessageParameters> messageParameters =
                getMessageParameters(getDecider(type), combinedSubscriptions.get(type));
            allMessages.addAll(messageParameters);
        }
        deciderService.putMessagesOnAssemblerQueue(allMessages, 0, true);
    }
    
    private void doDailyDigestUngrouped(String digestTime) {
        SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptionsPerEventType = subscriptionDao.getDailyDigestUngrouped(digestTime);
        for (SmartNotificationEventType type : subscriptionsPerEventType.keySet()) {
            List<SmartNotificationMessageParameters> messageParameters =
                getMessageParameters(getDecider(type), subscriptionsPerEventType.get(type));
            deciderService.putMessagesOnAssemblerQueue(messageParameters, 0, false);
        }
    }
    
    private List<SmartNotificationMessageParameters> getMessageParameters(SmartNotificationDecider decider,
            Set<SmartNotificationSubscription> subscriptions) {
        
        Instant now = Instant.now();
        Instant oneDayAgo = now.minus(Duration.standardDays(1));
        List<SmartNotificationEvent> events = getEvents(decider, new Range<>(oneDayAgo, false, now, true)); //retrieved events correctly
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents =
            decider.mapSubscriptionsToEvents(subscriptions, events);
        List<SmartNotificationMessageParameters> messageParameters =
            MessageParametersHelper.getMessageParameters(decider.getEventType(), subscriptionsToEvents, 0);
        return messageParameters;
    }
    
    private List<SmartNotificationEvent> getEvents(SmartNotificationDecider decider, Range<Instant> range){

        List<SmartNotificationEvent> events = eventDao.getEventsByTypeAndDate(decider.getEventType(), range);
        return decider.validate(events);
    }
    
    private SmartNotificationDecider getDecider(SmartNotificationEventType type){
        return deciders.stream().filter(d -> d.getEventType() == type).findFirst().get();
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        try {
            if (message instanceof ObjectMessage) {
                Serializable object = objMessage.getObject();
                if (object instanceof DailyDigestTestParams) {
                    Integer hour = ((DailyDigestTestParams) object).getHour();
                    DateTime digestTime = DateTime.now().withHourOfDay(hour).withMinuteOfHour(0);
                    doDailyDigest(digestTime);
                }
            }
        } catch (JMSException e) {
            log.error("Unable to extract message", e);
        } catch (Exception e) {
            log.error("Unable to process message", e);
        }
    }
}
