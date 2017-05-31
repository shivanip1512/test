package com.cannontech.dr.controlaudit;

import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.google.common.collect.ImmutableMap;

public enum ControlEventDeviceStatus {

    UNKNOWN(null, 0),
    SUCCESS_RECEIVED(EventPhase.NOT_STARTED, 1),
    SUCCESS_STARTED(EventPhase.PHASE_1, 2),
    SUCCESS_COMPLETED(EventPhase.COMPLETED, 3);

    private EventPhase eventPhase;
    private int messageOrder;
    private final static ImmutableMap<EventPhase, ControlEventDeviceStatus> lookupByEventPhase;

    ControlEventDeviceStatus(EventPhase eventPhase, int messageOrder) {
        this.eventPhase = eventPhase;
        this.messageOrder = messageOrder;
    }

    static {
        ImmutableMap.Builder<EventPhase, ControlEventDeviceStatus> eventPhaseBuilder = ImmutableMap.builder();
        for (ControlEventDeviceStatus controlEventDeviceStatus : values()) {
            if (controlEventDeviceStatus.eventPhase != null) {
                eventPhaseBuilder.put(controlEventDeviceStatus.eventPhase, controlEventDeviceStatus);
            }
        }
        lookupByEventPhase = eventPhaseBuilder.build();

    }

    public static ControlEventDeviceStatus getDeviceStatus(EventPhase eventPhase) {
        return lookupByEventPhase.get(eventPhase);
    }

    public int getMessageOrder() {
        return messageOrder;
    }

}
