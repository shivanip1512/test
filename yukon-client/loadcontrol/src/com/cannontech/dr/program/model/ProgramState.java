package com.cannontech.dr.program.model;

import java.util.Map;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ProgramState {
    ACTIVE(LMProgramBase.STATUS_FULL_ACTIVE),
    PARTIALLY_ACTIVE(LMProgramBase.STATUS_ACTIVE),
    INACTIVE(LMProgramBase.STATUS_INACTIVE),
    MANUAL_ACTIVE(LMProgramBase.STATUS_MANUAL_ACTIVE),
    SCHEDULED(LMProgramBase.STATUS_SCHEDULED),
    CONTROL_ATTEMPT(LMProgramBase.STATUS_CNTRL_ATTEMPT),
    NOTIFIED(LMProgramBase.STATUS_NOTIFIED),
    STOPPING(LMProgramBase.STATUS_STOPPING),
    NON_CONTROL(LMProgramBase.STATUS_NON_CNTRL),
    TIMED_ACTIVE(LMProgramBase.STATUS_TIMED_ACTIVE),
    ;

    private int programStateId;

    private ProgramState(int programStateId) {
        this.programStateId = programStateId;
    }

    private static Map<Integer, ProgramState> lookupByLegacyId;
    static {
        Builder<Integer, ProgramState> builder = ImmutableMap.builder();
        for (ProgramState programState : values()) {
            builder.put(programState.programStateId, programState);
        }
        lookupByLegacyId = builder.build();
    }

    public static ProgramState valueOf(int stateId) {
        return lookupByLegacyId.get(stateId);
    }
}
