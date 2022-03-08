package com.cannontech.services.smartNotification.service.impl;

import static com.cannontech.common.smartNotification.model.SmartNotificationEventType.DEVICE_DATA_MONITOR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
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
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters.ProcessingType;
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
    private static Logger snLogger = YukonLogManager.getSmartNotificationsLogger(SmartNotificationDailyDigestService.class);
   
    @PostConstruct
    private void scheduleDailyDigest() {
        int minutesToNextRun = 60 - DateTime.now().getMinuteOfHour();
        snLogger.info("Scheduling Daily Digest. Minutes to the next run:{} ", minutesToNextRun);
        scheduledExecutor.scheduleWithFixedDelay(() -> doDailyDigest(DateTime.now()), minutesToNextRun, 60, TimeUnit.MINUTES);
    }
    
    private void doDailyDigest(DateTime now) {
        try {
            Range<Instant> range = getDailyRange();
            String digestTime = now.withMinuteOfHour(0).toString("H:mm"); //Digest should always be on the hour  
            doDailyDigestGrouped(digestTime, range);
            doDailyDigestUngrouped(digestTime, range);
        } catch (Exception e) {
            snLogger.error("Unexpected exception occurred while processing Smart Notification Daily Digest.", e);
        }
    }
    
    /**
     * Sends 1 email for all subscription types and monitors
     */
    private void doDailyDigestGrouped(String digestTime, Range<Instant> range) {
        SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptions = subscriptionDao
                .getDailyDigestGrouped(digestTime);
        SetMultimap<Integer, SmartNotificationSubscription> ddmSubscriptions = subscriptionDao
                .getDailyDigestDeviceDataMonitorGrouped(digestTime);
        
        
        List<List<SmartNotificationMessageParameters>> allMessages = getAllMassages(range, digestTime, subscriptions, ddmSubscriptions, "One email per person");
       
        //group by recipient
        Map<String, List<SmartNotificationMessageParameters>> messages = new HashMap<>();
        
        allMessages.stream().flatMap(List::stream).forEach(param -> {
            param.getRecipients().forEach(recipient -> {
                SmartNotificationMessageParameters message = new SmartNotificationMessageParameters(param.getType(),
                        param.getMedia(),
                        param.getVerbosity(), 
                        List.of(recipient), 
                        param.getEvents(), 
                        param.getProcessingType());
                if (!messages.containsKey(recipient)) {
                    messages.put(recipient, new ArrayList<>());
                }
                messages.get(recipient).add(message);
            });
        });
        
        messages.forEach((recipient, params) -> {
            // each monitor is its own type
            snLogger.info("Generating one DAILY email for Recipient {} EventRange:{} including {} notification types", recipient,
                    range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS") + " - "
                            + range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                    params.size());
            deciderService.putMessagesOnAssemblerQueue(params, 0, true, digestTime);
        });
    }

    /**
     * Sends 1 email for each subscription type and 1 email per monitor
     */
    private void doDailyDigestUngrouped(String digestTime, Range<Instant> range) {
        SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptions = subscriptionDao
                .getDailyDigestUngrouped(digestTime);
        SetMultimap<Integer, SmartNotificationSubscription> ddmSubscriptions = subscriptionDao
                .getDailyDigestDeviceDataMonitorUngrouped(digestTime);
        List<List<SmartNotificationMessageParameters>> messages = getAllMassages(range, digestTime, subscriptions,
                ddmSubscriptions, "One email per type");
        messages.forEach(messageParameters -> deciderService.putMessagesOnAssemblerQueue(messageParameters, 0, false, digestTime));
    }

    private List<List<SmartNotificationMessageParameters>> getAllMassages(Range<Instant> range, String digestTime,
            SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> subscriptions,
            SetMultimap<Integer, SmartNotificationSubscription> ddmSubscriptions, String debugString) {
      
        List<List<SmartNotificationMessageParameters>> allMessages = new ArrayList<>();
        // all event types
        for (SmartNotificationEventType type : subscriptions.keySet()) {
            List<SmartNotificationEvent> events = getDecider(type).validate(eventDao.getEventsByTypeAndDate(type, range));
            List<SmartNotificationMessageParameters> messageParameters = getMessageParameters(type, subscriptions.get(type),
                    events);
            snLogger.info("DigestTime:{} EventRange:{} ({}) Type:{} MessageParameters:{}", digestTime,
                    range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS") + " - "
                            + range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                    debugString,
                    type, messageParameters.size());
            allMessages.add(messageParameters);
        }

        // device data monitors
        for (Integer monitorId : ddmSubscriptions.keySet()) {
            List<SmartNotificationEvent> events = getDecider(DEVICE_DATA_MONITOR)
                    .validate(eventDao.getEventsByMonitorIdAndDate(monitorId, range));
            List<SmartNotificationMessageParameters> messageParameters = getMessageParameters(DEVICE_DATA_MONITOR,
                    ddmSubscriptions.get(monitorId), events);
            snLogger.info("DigestTime:{} EventRange:{} ({}) MonitorId:{} MessageParameters:{}", digestTime,
                    range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS") + " - "
                            + range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                    debugString, monitorId, messageParameters.size());
            allMessages.add(messageParameters);
        }
        return allMessages;
    }
    
    private Range<Instant> getDailyRange() {
        Instant now = new DateTime().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toInstant();
        Instant oneDayAgo = now.minus(Duration.standardDays(1));
        return new Range<>(oneDayAgo, false, now, true);
    }

    private List<SmartNotificationMessageParameters> getMessageParameters(SmartNotificationEventType type,
            Set<SmartNotificationSubscription> subscriptions, List<SmartNotificationEvent> events) {
        SmartNotificationDecider decider = getDecider(type);
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionsToEvents =
            decider.mapSubscriptionsToEvents(subscriptions, events);
        List<SmartNotificationMessageParameters> messageParameters =
            MessageParametersHelper.getMessageParameters(decider.getEventType(), subscriptionsToEvents, 0, ProcessingType.DIGEST);
        return messageParameters;
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
                    String digestTime = ((DailyDigestTestParams) object).getHour() + ":00";
                    Instant now = new DateTime().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).plusHours(1)
                            .toInstant();
                    Instant oneDayAgo = now.minus(Duration.standardDays(1));
                    Range<Instant> range = new Range<>(oneDayAgo, false, now, true);
                    doDailyDigestGrouped(digestTime, range);
                    doDailyDigestUngrouped(digestTime, range);
                }
            }
        } catch (Exception e) {
            snLogger.error("Unable to process message", e);
        }
    }
}
