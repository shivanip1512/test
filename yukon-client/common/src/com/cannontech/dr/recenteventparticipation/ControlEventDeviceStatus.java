package com.cannontech.dr.recenteventparticipation;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.itron.service.impl.ItronLoadControlEventStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public enum ControlEventDeviceStatus {

    UNKNOWN(null, new ArrayList<>(), 0),
    SUCCESS_RECEIVED(EventPhase.NOT_STARTED, ItronLoadControlEventStatus.EVENT_RECEIVED, 1),
    SUCCESS_STARTED(EventPhase.PHASE_1, ItronLoadControlEventStatus.EVENT_STARTED, 2),
    SUCCESS_COMPLETED(EventPhase.COMPLETED, Lists.newArrayList(ItronLoadControlEventStatus.EVENT_CANCELLED, ItronLoadControlEventStatus.EVENT_COMPLETED), 3);

    private EventPhase eventPhase;
    private List<ItronLoadControlEventStatus> itronStatus;
    private int messageOrder;
    private static final ImmutableMap<EventPhase, ControlEventDeviceStatus> lookupByEventPhase;
    private static final ImmutableMap<ItronLoadControlEventStatus, ControlEventDeviceStatus> lookupByItronStatus;
    private static final ImmutableSet<ControlEventDeviceStatus> allDeviceStatus;
    
    ControlEventDeviceStatus(EventPhase eventPhase, ItronLoadControlEventStatus status, int messageOrder) {
        this.eventPhase = eventPhase;
        itronStatus = Lists.newArrayList(status);
        this.messageOrder = messageOrder;
    }
    
    ControlEventDeviceStatus(EventPhase eventPhase, List<ItronLoadControlEventStatus> statuses, int messageOrder) {
        this.eventPhase = eventPhase;
        itronStatus = statuses;
        this.messageOrder = messageOrder;
    }

    static {
        ImmutableMap.Builder<EventPhase, ControlEventDeviceStatus> eventPhaseBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<ItronLoadControlEventStatus, ControlEventDeviceStatus> itronStatusBuilder = ImmutableMap.builder();
        ImmutableSet.Builder<ControlEventDeviceStatus> eventDeviceStatusBuilder = ImmutableSet.builder();
        
        for (ControlEventDeviceStatus controlEventDeviceStatus : values()) {
            if (controlEventDeviceStatus.eventPhase != null) {
                eventPhaseBuilder.put(controlEventDeviceStatus.eventPhase, controlEventDeviceStatus);
                eventDeviceStatusBuilder.add(controlEventDeviceStatus);
            }
            for (ItronLoadControlEventStatus itronStatus : controlEventDeviceStatus.itronStatus) {
                itronStatusBuilder.put(itronStatus, controlEventDeviceStatus);
            }
        }
        lookupByEventPhase = eventPhaseBuilder.build();
        lookupByItronStatus = itronStatusBuilder.build();
        allDeviceStatus = eventDeviceStatusBuilder.build();
    }

    public static ControlEventDeviceStatus getDeviceStatus(EventPhase eventPhase) {
        return lookupByEventPhase.get(eventPhase);
    }
    
    public static ControlEventDeviceStatus getDeviceStatus(ItronLoadControlEventStatus itronStatus) {
        return lookupByItronStatus.get(itronStatus);
    }

    public int getMessageOrder() {
        return messageOrder;
    }

    public EventPhase getEventPhase(){
        return eventPhase;
    }
    
    public List<ItronLoadControlEventStatus> getItronLoadControlEventStatus() {
        return itronStatus;
    }

    public static ImmutableSet<ControlEventDeviceStatus> getAllDeviceStatus() {
        return allDeviceStatus;
    }

}
