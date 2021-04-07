package com.cannontech.services.smartNotification.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class SmartNotifDeviceDataMonitorDecider extends SmartNotificationDecider {

    {
        eventType = SmartNotificationEventType.DEVICE_DATA_MONITOR;
    }
    
    @Autowired private MonitorCacheService monitorCacheService;
    
    @Override
    public List<SmartNotificationEvent> validate(List<SmartNotificationEvent> events) {
        return events.stream().
                filter(e -> isValidMonitor(e.getParameters()) && isValidDevice(e.getParameters()))
                .collect(Collectors.toList());
    }

    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> getSubscriptionsForEvents(
            List<SmartNotificationEvent> allEvents, SmartNotificationFrequency frequency) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if(allEvents.isEmpty()){
            return subscriptions;
        }
        Set<Integer> monitorIds = allEvents.stream().map(e -> DeviceDataMonitorEventAssembler.getMonitorId(e.getParameters())).collect(
                Collectors.toSet());         
        List<SmartNotificationSubscription> allSubscriptions = subscriptionDao.getSubscriptions(eventType,
            DeviceDataMonitorEventAssembler.MONITOR_ID, new ArrayList<>(monitorIds), frequency);
        
        SetMultimap<Integer, SmartNotificationSubscription> monitorIdToSubscription = HashMultimap.create();
        allSubscriptions.forEach(s-> {
            monitorIdToSubscription.put(DeviceDataMonitorEventAssembler.getMonitorId(s.getParameters()), s);
        });
        
        allEvents.forEach(e -> {
            int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(e.getParameters());
            if (monitorIdToSubscription.containsKey(monitorId)) {
                monitorIdToSubscription.get(monitorId).forEach(s -> subscriptions.put(s, e));
            }
        });
        return subscriptions;
    }
    
    private boolean isValidMonitor(Map<String, Object> parameters) {
        int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(parameters);
        return monitorCacheService.getDeviceMonitor(monitorId) != null;
    }

    private boolean isValidDevice(Map<String, Object> parameters) {
        int paoId = DeviceDataMonitorEventAssembler.getPaoId(parameters);
        return cache.getAllPaosMap().containsKey(paoId);
    }

    @Override
    public SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> mapSubscriptionsToEvents(
            Set<SmartNotificationSubscription> allSubscriptions, List<SmartNotificationEvent> allEvents) {
        SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptions = HashMultimap.create();
        if (allEvents.isEmpty()) {
            return subscriptions;
        }
        SetMultimap<Integer, SmartNotificationEvent> deviceDataMonitorMap = HashMultimap.create();
        allEvents.forEach(event -> {
            Integer monitorId = DeviceDataMonitorEventAssembler.getMonitorId(event.getParameters());
            deviceDataMonitorMap.put(monitorId, event);
        });
        allSubscriptions.forEach(sub -> {
            Integer monitorId = DeviceDataMonitorEventAssembler.getMonitorId(sub.getParameters());
            subscriptions.putAll(sub, deviceDataMonitorMap.get(monitorId));
        });
        return subscriptions;
    }

    @Override
    protected List<SmartNotificationEvent> getUnprocessedGroupedEvents(String cacheKey) {
        return eventDao.getUnprocessedGroupedEvents(eventType, "monitorId", cacheKey.replace(eventType + "_", ""));
    }

    @Override
    protected String getCacheKey(SmartNotificationSubscription subscription) {
        return eventType + "_" + DeviceDataMonitorEventAssembler.getMonitorId(subscription.getParameters());
    }
}
