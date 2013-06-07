package com.cannontech.dr.controlarea.model;

import java.util.Map;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ControlAreaState {
    ACTIVE(ControlAreaItem.STATE_FULLY_ACTIVE),
    PARTIALLY_ACTIVE(ControlAreaItem.STATE_PARTIALLY_ACTIVE),
    MANUAL_ACTIVE(ControlAreaItem.STATE_MANUAL_ACTIVE),
    CONTROL_ATTEMPT(ControlAreaItem.STATE_CNTRL_ATTEMPT),
    FULLY_SCHEDULED(ControlAreaItem.STATE_FULLY_SCHEDULED),
    PARTIALLY_SCHEDULED(ControlAreaItem.STATE_PARTIALLY_SCHEDULED),
    INACTIVE(ControlAreaItem.STATE_INACTIVE),
    ;

    private int controlAreaStateId;

    private ControlAreaState(int controlAreaStateId) {
        this.controlAreaStateId = controlAreaStateId;
    }

    private static Map<Integer, ControlAreaState> lookupByLegacyId;
    static {
        Builder<Integer, ControlAreaState> builder = ImmutableMap.builder();
        for (ControlAreaState controlAreaState : values()) {
            builder.put(controlAreaState.controlAreaStateId, controlAreaState);
        }
        lookupByLegacyId = builder.build();
    }

    public static ControlAreaState valueOf(int stateId) {
        return lookupByLegacyId.get(stateId);
    }
}
