package com.cannontech.dr.program.model;

import java.util.Map;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ProgramState implements DisplayableEnum{
    ACTIVE(Program.STATUS_FULL_ACTIVE),
    PARTIALLY_ACTIVE(Program.STATUS_ACTIVE),
    INACTIVE(Program.STATUS_INACTIVE),
    MANUAL_ACTIVE(Program.STATUS_MANUAL_ACTIVE),
    TIMED_ACTIVE(Program.STATUS_TIMED_ACTIVE),
    SCHEDULED(Program.STATUS_SCHEDULED),
    CONTROL_ATTEMPT(Program.STATUS_CNTRL_ATTEMPT),
    NOTIFIED(Program.STATUS_NOTIFIED),
    STOPPING(Program.STATUS_STOPPING),
    NON_CONTROL(Program.STATUS_NON_CNTRL),
    ;
    
    private final String keyPrefix = "yukon.web.modules.dr.program.value.STATE.";
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
    
    public String getStyleString() {
        return "PROGRAM_STATE_" + toString();
    }
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
