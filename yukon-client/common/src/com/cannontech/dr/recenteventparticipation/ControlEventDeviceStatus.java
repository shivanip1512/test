package com.cannontech.dr.recenteventparticipation;

import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum ControlEventDeviceStatus {

    UNKNOWN(null, 0),
    SUCCESS_RECEIVED(EventPhase.NOT_STARTED, 1),
    SUCCESS_STARTED(EventPhase.PHASE_1, 2),
    SUCCESS_COMPLETED(EventPhase.COMPLETED, 3);

    private EventPhase eventPhase;
    private int messageOrder;
    private final static ImmutableMap<EventPhase, ControlEventDeviceStatus> lookupByEventPhase;
    private final static ImmutableSet<ControlEventDeviceStatus> allDeviceStatus;

    ControlEventDeviceStatus(EventPhase eventPhase, int messageOrder) {
        this.eventPhase = eventPhase;
        this.messageOrder = messageOrder;
    }

    static {
        ImmutableMap.Builder<EventPhase, ControlEventDeviceStatus> eventPhaseBuilder = ImmutableMap.builder();
        ImmutableSet.Builder<ControlEventDeviceStatus> eventDeviceStatusBuilder = ImmutableSet.builder();
        
        for (ControlEventDeviceStatus controlEventDeviceStatus : values()) {
            if (controlEventDeviceStatus.eventPhase != null) {
                eventPhaseBuilder.put(controlEventDeviceStatus.eventPhase, controlEventDeviceStatus);
                eventDeviceStatusBuilder.add(controlEventDeviceStatus);
            }
        }
        lookupByEventPhase = eventPhaseBuilder.build();
        allDeviceStatus = eventDeviceStatusBuilder.build();
    }

    public static ControlEventDeviceStatus getDeviceStatus(EventPhase eventPhase) {
        return lookupByEventPhase.get(eventPhase);
    }

    public int getMessageOrder() {
        return messageOrder;
    }

    public EventPhase getEventPhase(){
        return eventPhase;
    }

    public static ImmutableSet<ControlEventDeviceStatus> getAllDeviceStatus() {
        return allDeviceStatus;
    }

}
