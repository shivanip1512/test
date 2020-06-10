package com.cannontech.web.tools.points.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public enum AlarmState {
    NON_UPDATED("Non-updated"),
    RATE_OF_CHANGE("Rate Of Change"),
    LIMIT_SET_1("Limit Set 1"),
    LIMIT_SET_2("Limit Set 2"),
    HIGHT_REASONABILITY("High Reasonability"),
    LOW_REASONABILITY("Low Reasonability"),
    LOW_LIMIT_1("Low Limit 1"),
    LOW_LIMIT_2("Low Limit 2"),
    HIGH_LIMIT_1("High Limit 1"),
    HIGH_LIMIT_2("High Limit 2"),
    STALE("Stale"),
    ABNORMAL("Abnormal"),
    UNCOMMONED_STATE_CHANGED("Uncommanded State Change"),
    COMMAND_FAILURE("Command Failure");

    private String value;
    private final static ImmutableMap<String, AlarmState> lookupByAlarmState;
    private static final Logger log = YukonLogManager.getLogger(AlarmState.class);

    static {
        try {
            ImmutableMap.Builder<String, AlarmState> alarmStateBuilder = ImmutableMap.builder();
            for (AlarmState type : values()) {
                alarmStateBuilder.put(type.value, type);
            }
            lookupByAlarmState = alarmStateBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or value.", e);
            throw e;
        }
    }

    public String getValue() {
        return this.value;
    }

    AlarmState(String value) {
        this.value = value;
    }
    
    private final static ImmutableList<AlarmState> statusAlarmStates;
    private final static ImmutableList<AlarmState> otherAlarmStates;

    static {
        otherAlarmStates = ImmutableList.of(NON_UPDATED,
                RATE_OF_CHANGE,
                LIMIT_SET_1,
                LIMIT_SET_2,
                HIGHT_REASONABILITY,
                LOW_REASONABILITY,
                LOW_LIMIT_1,
                LOW_LIMIT_2,
                HIGH_LIMIT_1,
                HIGH_LIMIT_2,
                STALE);
        statusAlarmStates = ImmutableList.of(NON_UPDATED,
                ABNORMAL,
                UNCOMMONED_STATE_CHANGED,
                COMMAND_FAILURE,
                STALE);
    }

    public static ImmutableList<AlarmState> getStatusAlarmStates() {
        return statusAlarmStates;
    }

    public static ImmutableList<AlarmState> getOtherAlarmStates() {
        return otherAlarmStates;
    }

    public static AlarmState getAlarmStateValue(String value) {
        AlarmState alarmState = lookupByAlarmState.get(value);
        checkArgument(alarmState != null, alarmState);
        return alarmState;
    }
}
