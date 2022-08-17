package com.cannontech.services.smartNotification.service.impl;

import java.util.List;
import java.util.Set;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class SmartNotificationWatchdogDecider extends SmartNotificationDecider {

    {
        eventType = SmartNotificationEventType.YUKON_WATCHDOG;
    }

    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> allEvents, SmartNotificationFrequency frequency) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if (allEvents.isEmpty()) {
            return subscriptions;
        }
        List<SmartNotificationSubscription> allSubscriptions = subscriptionDao.getSubscriptions(eventType, frequency);
        allSubscriptions.forEach(s -> {
            subscriptions.putAll(s, allEvents);
        });
        return subscriptions;
    }

    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> mapSubscriptionsToEvents(
            Set<SmartNotificationSubscription> allSubscriptions, List<SmartNotificationEvent> allEvents) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if (allEvents.isEmpty()) {
            return subscriptions;
        }
        allSubscriptions.forEach(sub -> {
            subscriptions.putAll(sub, allEvents);
        });
        return subscriptions;
    }

    @Override
    public List<SmartNotificationEvent> validate(List<SmartNotificationEvent> events) {
        return events;
    }
}
