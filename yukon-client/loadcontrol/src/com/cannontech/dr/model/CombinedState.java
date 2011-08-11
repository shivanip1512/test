package com.cannontech.dr.model;

import com.cannontech.dr.controlarea.model.ControlAreaState;
import com.cannontech.dr.loadgroup.model.LoadGroupState;
import com.cannontech.dr.program.model.ProgramState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CombinedState {
    // "active" forms
    ACTIVE(ControlAreaState.ACTIVE, ProgramState.ACTIVE, LoadGroupState.ACTIVE),
    PARTIALLY_ACTIVE(ControlAreaState.PARTIALLY_ACTIVE, ProgramState.PARTIALLY_ACTIVE, null),
    MANUAL_ACTIVE(ControlAreaState.MANUAL_ACTIVE, ProgramState.MANUAL_ACTIVE, null),
    TIMED_ACTIVE(null, ProgramState.TIMED_ACTIVE, null),
    SCHEDULED(ControlAreaState.FULLY_SCHEDULED, ProgramState.SCHEDULED, null),
    PARTIALLY_SCHEDULED(ControlAreaState.PARTIALLY_SCHEDULED, null, null),
    CONTROL_ATTEMPT(ControlAreaState.CONTROL_ATTEMPT, ProgramState.CONTROL_ATTEMPT, null),
    NOTIFIED(null, ProgramState.NOTIFIED, null),
    STOPPING(null, ProgramState.STOPPING, null),
    ACTIVE_PENDING(null, null, LoadGroupState.ACTIVE_PENDING),

    // "inactive" forms
    INACTIVE(ControlAreaState.INACTIVE, ProgramState.INACTIVE, LoadGroupState.INACTIVE),
    NON_CONTROL(null, ProgramState.NON_CONTROL, null),
    INACTIVE_PENDING(null, null, LoadGroupState.INACTIVE_PENDING),
    ;

    private LoadGroupState matchingLoadGroupState = null;
    private ProgramState matchingProgramState = null;
    private ControlAreaState matchingControlAreaState = null;

    private final static ImmutableMap<ControlAreaState, CombinedState> lookupByControlAreaState;
    private final static ImmutableMap<ProgramState, CombinedState> lookupByProgramState;
    private final static ImmutableMap<LoadGroupState, CombinedState> lookupByLoadGroupState;

    static {
        Builder<ControlAreaState, CombinedState> controlAreaBuilder = ImmutableMap.builder();
        Builder<ProgramState, CombinedState> programBuilder = ImmutableMap.builder();
        Builder<LoadGroupState, CombinedState> loadGroupBuilder = ImmutableMap.builder();

        for (CombinedState combinedState : values()) {
            if (combinedState.matchingControlAreaState != null) {
                controlAreaBuilder.put(combinedState.matchingControlAreaState, combinedState);
            }
            if (combinedState.matchingProgramState != null) {
                programBuilder.put(combinedState.matchingProgramState, combinedState);
            }
            if (combinedState.matchingLoadGroupState != null) {
                loadGroupBuilder.put(combinedState.matchingLoadGroupState, combinedState);
            }
        }

        lookupByControlAreaState = controlAreaBuilder.build();
        lookupByProgramState = programBuilder.build();
        lookupByLoadGroupState = loadGroupBuilder.build();
    }

    private CombinedState(ControlAreaState matchingControlAreaState,
            ProgramState matchingProgramState,
            LoadGroupState matchingLoadGroupState) {
        this.matchingControlAreaState = matchingControlAreaState;
        this.matchingProgramState = matchingProgramState;
        this.matchingLoadGroupState = matchingLoadGroupState;
    }

    public static CombinedState forControlAreaState(ControlAreaState controlAreaState) {
        return lookupByControlAreaState.get(controlAreaState);
    }

    public static CombinedState forProgramState(ProgramState programState) {
        return lookupByProgramState.get(programState);
    }

    public static CombinedState forLoadGroupState(LoadGroupState loadGroupState) {
        return lookupByLoadGroupState.get(loadGroupState);
    }
}
