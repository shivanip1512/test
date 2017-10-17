package com.cannontech.services.smartNotification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.google.common.collect.SetMultimap;

public class SmartNotificationDailyDigestService{

    @Autowired protected SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired protected SmartNotificationEventDao eventDao;
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private List<SmartNotificationDecider> deciders;
    @Autowired private SmartNotificationDeciderService deciderService;
    
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDailyDigestService.class);
   
    @PostConstruct
    private void scheduleDailyDigest() {
        log.info("Scheduling Daily Digest");
        long minutesToNextRun = 60 - Integer.parseInt(new DateTime().toString("mm"));
        scheduledExecutor.scheduleWithFixedDelay(() -> {

            log.info("Running Daily Digest at "+ new DateTime().toString("MM-dd-yyyy HH:mm:ss"));
            String runTimeInMinutes = new DateTime().toString("HH:mm");;
            List<SmartNotificationMessageParameters> allMessages = new ArrayList<>();
            SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> combinedSubscriptions = subscriptionDao.getDailyDigestGrouped(runTimeInMinutes);
            for (SmartNotificationEventType type : combinedSubscriptions.keySet()) {
                List<SmartNotificationMessageParameters> messageParameters =
                    getMessageParameters(getDecider(type), combinedSubscriptions.get(type));
                allMessages.addAll(messageParameters);
            }
            deciderService.putMessagesOnAssemblerQueue(allMessages, 0, true);
            
            SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptionsPerEventType = subscriptionDao.getDailyDigestUngrouped(runTimeInMinutes);
            for (SmartNotificationEventType type : subscriptionsPerEventType.keySet()) {
                List<SmartNotificationMessageParameters> messageParameters =
                    getMessageParameters(getDecider(type), subscriptionsPerEventType.get(type));
                deciderService.putMessagesOnAssemblerQueue(messageParameters, 0, true);
            }
            
            
        }, minutesToNextRun, 60, TimeUnit.MINUTES);
    }
    
    private List<SmartNotificationMessageParameters> getMessageParameters(SmartNotificationDecider decider,
            Set<SmartNotificationSubscription> subcriptions) {
        List<SmartNotificationEvent> events = getEvents(decider, new Range<>(Instant.now().minus(TimeUnit.DAYS.toMillis(1)), false, Instant.now(), true));
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents =
            decider.mapSubscriptionsToEvents(subcriptions, events);
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
}
