package com.cannontech.dr.loadgroup.model;

import java.util.Map;

import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum LoadGroupState {
    ACTIVE(GroupBase.STATE_ACTIVE),
    ACTIVE_PENDING(GroupBase.STATE_ACTIVE_PENDING),
    INACTIVE(GroupBase.STATE_INACTIVE),
    INACTIVE_PENDING(GroupBase.STATE_INACTIVE_PENDING),
    ;

    private int loadGroupStateId;

    private LoadGroupState(int loadGroupStateId) {
        this.loadGroupStateId = loadGroupStateId;
    }

    private static Map<Integer, LoadGroupState> lookupByLegacyId;
    static {
        Builder<Integer, LoadGroupState> builder = ImmutableMap.builder();
        for (LoadGroupState loadGroupState : values()) {
            builder.put(loadGroupState.loadGroupStateId, loadGroupState);
        }
        lookupByLegacyId = builder.build();
    }

    public static LoadGroupState valueOf(int stateId) {
        return lookupByLegacyId.get(stateId);
    }
}
