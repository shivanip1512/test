package com.cannontech.services.smartNotification.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.smartNotification.model.InfrastructureWarningsParametersAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class SmartNotifInfrastructureWarningsDecider extends SmartNotificationDecider {

    {
        eventType = SmartNotificationEventType.INFRASTRUCTURE_WARNING;
    }

    @Override
    public List<SmartNotificationEvent> validate(List<SmartNotificationEvent> events) {
        return events.stream().filter(e -> isValidDevice(e)).collect(Collectors.toList());
    }
   
    private boolean isValidDevice(SmartNotificationEvent event) {
        int paoId = InfrastructureWarningsParametersAssembler.getPaoId(event.getParameters());
        return cache.getAllPaosMap().containsKey(paoId);
    }

    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> allEvents, SmartNotificationFrequency frequency) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if(allEvents.isEmpty()){
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
        if(allEvents.isEmpty()){
            return subscriptions;
        }
        //add subscriptions and events to the multimap
        return subscriptions;
    }
}
