package com.cannontech.services.smartNotification.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class SmartNotificationDailyDigestService{

    @Autowired protected SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private List<SmartNotificationDecider> deciders;
    @Autowired private SmartNotificationDeciderService deciderService;
    
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationDailyDigestService.class);
   
    @PostConstruct
    private void scheduleDailyDigest() {
        // change to run every hour, on the hour 1,2,3,4,5 etc
        log.info("Scheduling Daily Digest");
        scheduledExecutor.scheduleWithFixedDelay(() -> {

            log.info("Running Daily Digest at "+ new DateTime().toString("MM-dd-yyyy HH:mm:ss"));
            // when this schedule runs get time and convert it to this format: 00:00, 01:00, 02:00 ..... 23:00
            // (minutes:seconds);

            String runTimeInMinutes = "01:00";

            // 1) select * from SmartNotificationSub join to UserPreference
            // on UserTd and Name = UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME and
            // Value=runTimeInMinutes ("01:00");

            // result of query 1
            List<SmartNotificationMessageParameters> allMessages = new ArrayList<>();
            SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> combinedSubscriptions = HashMultimap.create();
            for (SmartNotificationEventType type : combinedSubscriptions.keySet()) {
                List<SmartNotificationMessageParameters> messageParameters =
                    getMessageParameters(getDecider(type), combinedSubscriptions.get(type));
                allMessages.addAll(messageParameters);
            }
            deciderService.putMessagesOnAssemblerQueue(allMessages, true);
            
            // 2) select * from SmartNotificationSub join to SmartNotificationEventParam
            // on eventId Name = "sendTime" and value=runTimeInMinutes ("01:00");


            SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptionsPerEventType =
                HashMultimap.create();
            for (SmartNotificationEventType type : subscriptionsPerEventType.keySet()) {
                List<SmartNotificationMessageParameters> messageParameters =
                    getMessageParameters(getDecider(type), subscriptionsPerEventType.get(type));
                deciderService.putMessagesOnAssemblerQueue(messageParameters, false);
            }
            
            
        }, 1, 1, TimeUnit.MINUTES);
    }
    
    private List<SmartNotificationMessageParameters> getMessageParameters(SmartNotificationDecider decider,
            Set<SmartNotificationSubscription> subcriptions) {
        List<SmartNotificationEvent> events = getEvents(decider, new Range<Instant>(null, false, null, false));
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents =
            decider.mapSubscriptionsToEvents(subcriptions, events);
        List<SmartNotificationMessageParameters> messageParameters =
            MessageParametersHelper.getMessageParameters(decider.getEventType(), subscriptionsToEvents);
        return messageParameters;
    }
    
    private List<SmartNotificationEvent> getEvents(SmartNotificationDecider decider, Range<Instant> range){
        // add query in  SmartNotificationEventDao to return events by date range and event type;
        decider.getEventType();
        List<SmartNotificationEvent> events = new ArrayList<>();
        return decider.validate(events);
    }
    
    private SmartNotificationDecider getDecider(SmartNotificationEventType type){
        return deciders.stream().filter(d -> d.getEventType() == type).findFirst().get();
    }
}
