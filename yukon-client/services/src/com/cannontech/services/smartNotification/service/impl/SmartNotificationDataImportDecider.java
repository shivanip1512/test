package com.cannontech.services.smartNotification.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.smartNotification.model.DataImportAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class SmartNotificationDataImportDecider extends SmartNotificationDecider {

    private final String ASSETIMPORT_RESULT_TYPE = "assetImportResultType";
    private final String IMPORTS_WITH_ERRORS = "IMPORTS_WITH_ERRORS";

    {
        eventType = SmartNotificationEventType.ASSET_IMPORT;
    }

    /**
     * This method is used to map subscriptions with events . As part of this method we are also filtering
     * events that will be send only when we have IMPORTS_WITH_ERRORS subscription.
     */
    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> allEvents, SmartNotificationFrequency frequency) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if (allEvents.isEmpty()) {
            return subscriptions;
        }

        List<SmartNotificationSubscription> allSubscriptions = subscriptionDao.getSubscriptions(eventType, frequency);
        List<SmartNotificationEvent> eventWithImportError = allEvents.stream()
                                                                     .filter(this::isEventWithError)
                                                                     .collect(Collectors.toList());
        allSubscriptions.forEach(s -> {
            if (s.getParameters().get(ASSETIMPORT_RESULT_TYPE).toString().equalsIgnoreCase(IMPORTS_WITH_ERRORS)) {
                subscriptions.putAll(s, eventWithImportError);
            } else {
                subscriptions.putAll(s, allEvents);
            }
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

    /**
     * This method will determine if the event have import with error or not.
     */
    private boolean isEventWithError(SmartNotificationEvent event) {
        return event.getParameters().entrySet()
                                    .stream()
                                    .filter(entry -> entry.getKey().equalsIgnoreCase(DataImportAssembler.FILES_WITH_ERROR))
                                    .anyMatch(entry -> StringUtils.isNotBlank(entry.getValue().toString()));
    }
}
