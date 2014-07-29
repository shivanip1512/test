package com.cannontech.dr.controlarea.model;

import java.util.Map;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ControlAreaState {
    ACTIVE(LMControlArea.STATE_FULLY_ACTIVE),
    PARTIALLY_ACTIVE(LMControlArea.STATE_PARTIALLY_ACTIVE),
    MANUAL_ACTIVE(LMControlArea.STATE_MANUAL_ACTIVE),
    CONTROL_ATTEMPT(LMControlArea.STATE_CNTRL_ATTEMPT),
    FULLY_SCHEDULED(LMControlArea.STATE_FULLY_SCHEDULED),
    PARTIALLY_SCHEDULED(LMControlArea.STATE_PARTIALLY_SCHEDULED),
    INACTIVE(LMControlArea.STATE_INACTIVE),
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
