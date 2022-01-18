package com.cannontech.dr.recenteventparticipation;

import java.util.*;
import java.util.stream.*;

import com.cannontech.dr.eatonCloud.model.EatonCloudEventStatus;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.itron.service.impl.ItronLoadControlEventStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum ControlEventDeviceStatus {

    UNKNOWN(0),
    FAILED_WILL_RETRY(1),
    FAILED(2),
    SUCCESS_RECEIVED(3),
    SUCCESS_STARTED(4),
    SUCCESS_COMPLETED(5);

    private int messageOrder;
    
    private static final ImmutableMap<EventPhase, ControlEventDeviceStatus> lookupByEventPhase;
    private static final ImmutableMap<ItronLoadControlEventStatus, ControlEventDeviceStatus> lookupByItronStatus;
    private static final ImmutableMap<EatonCloudEventStatus, ControlEventDeviceStatus> lookupByCloudStatus;
    private static final ImmutableSet<ControlEventDeviceStatus> allDeviceStatus;
    
    ControlEventDeviceStatus(int messageOrder) {
        this.messageOrder = messageOrder;
    }

    static {
        
        ImmutableMap.Builder<EventPhase, ControlEventDeviceStatus> eventPhaseLookupBuilder = ImmutableMap.builder();
        lookupByEventPhase = eventPhaseLookupBuilder
                .put(EventPhase.NOT_STARTED, SUCCESS_RECEIVED)
                .put(EventPhase.PHASE_1, SUCCESS_STARTED)
                .put(EventPhase.COMPLETED, SUCCESS_COMPLETED)
                .build();
        
        ImmutableMap.Builder<ItronLoadControlEventStatus, ControlEventDeviceStatus> itronStatusBuilder = ImmutableMap.builder();
        lookupByItronStatus = itronStatusBuilder
                .put(ItronLoadControlEventStatus.EVENT_RECEIVED, SUCCESS_RECEIVED)
                .put(ItronLoadControlEventStatus.EVENT_STARTED, SUCCESS_STARTED)
                .put(ItronLoadControlEventStatus.EVENT_CANCELLED, SUCCESS_COMPLETED)
                .put(ItronLoadControlEventStatus.EVENT_COMPLETED, SUCCESS_COMPLETED)
                .build();
        
        ImmutableMap.Builder<EatonCloudEventStatus, ControlEventDeviceStatus> eatonCloudStatusBuilder = ImmutableMap.builder();
        lookupByCloudStatus = eatonCloudStatusBuilder
                .put(EatonCloudEventStatus.RECEIVED, SUCCESS_RECEIVED)
                .put(EatonCloudEventStatus.STARTED, SUCCESS_STARTED)
                .put(EatonCloudEventStatus.COMPLETE, SUCCESS_COMPLETED)
                .put(EatonCloudEventStatus.CANCELED, SUCCESS_COMPLETED)
                .build();
        
        ImmutableSet.Builder<ControlEventDeviceStatus> eventDeviceStatusBuilder = ImmutableSet.builder();
        allDeviceStatus = eventDeviceStatusBuilder
                .addAll(Arrays.asList(ControlEventDeviceStatus.values()))
                .build();
        
    }

    public static ControlEventDeviceStatus getDeviceStatus(EventPhase eventPhase) {
        return lookupByEventPhase.get(eventPhase);
    }
    
    public static ControlEventDeviceStatus getDeviceStatus(ItronLoadControlEventStatus itronStatus) {
        return lookupByItronStatus.get(itronStatus);
    }
    
    public static ControlEventDeviceStatus getDeviceStatus(EatonCloudEventStatus cloudStatus) {
        return lookupByCloudStatus.get(cloudStatus);
    }

    public int getMessageOrder() {
        return messageOrder;
    }

    public EventPhase getEventPhase(){
        //returns null for UNKNOWN
        return lookupByEventPhase
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == this)
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }
    
    public List<ItronLoadControlEventStatus> getItronLoadControlEventStatus() {
        return lookupByItronStatus
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == this)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<EatonCloudEventStatus> getEatonCloudEventStatus() {
        return lookupByCloudStatus
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == this)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public static ImmutableSet<ControlEventDeviceStatus> getAllDeviceStatus() {
        return allDeviceStatus;
    }

}