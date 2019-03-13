package com.cannontech.dr.itron.service.impl;

import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.*;
import static com.cannontech.dr.itron.service.impl.ItronRelayDataLogs.LogType.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.dr.service.RelayLogInterval;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

enum ItronRelayDataLogs {

    RELAY_1_RUN_TIME(1, RUN_TIME, RELAY_1_RELAY_STATE, RELAY_1_RUN_TIME_DATA_LOG_5_MIN, RELAY_1_RUN_TIME_DATA_LOG_15_MIN, RELAY_1_RUN_TIME_DATA_LOG_30_MIN, RELAY_1_RUN_TIME_DATA_LOG),
    RELAY_2_RUN_TIME(2, RUN_TIME, RELAY_2_RELAY_STATE, RELAY_2_RUN_TIME_DATA_LOG_5_MIN, RELAY_2_RUN_TIME_DATA_LOG_15_MIN, RELAY_2_RUN_TIME_DATA_LOG_30_MIN, RELAY_2_RUN_TIME_DATA_LOG),
    RELAY_3_RUN_TIME(3, RUN_TIME, RELAY_3_RELAY_STATE, RELAY_3_RUN_TIME_DATA_LOG_5_MIN, RELAY_3_RUN_TIME_DATA_LOG_15_MIN, RELAY_3_RUN_TIME_DATA_LOG_30_MIN, RELAY_3_RUN_TIME_DATA_LOG),
    RELAY_4_RUN_TIME(4, RUN_TIME, RELAY_4_RELAY_STATE, RELAY_4_RUN_TIME_DATA_LOG_5_MIN, RELAY_4_RUN_TIME_DATA_LOG_15_MIN, RELAY_4_RUN_TIME_DATA_LOG_30_MIN, RELAY_4_RUN_TIME_DATA_LOG),

    RELAY_1_SHED_TIME(1, SHED_TIME, RELAY_1_SHED_STATUS, RELAY_1_SHED_TIME_DATA_LOG_5_MIN, RELAY_1_SHED_TIME_DATA_LOG_15_MIN, RELAY_1_SHED_TIME_DATA_LOG_30_MIN, RELAY_1_SHED_TIME_DATA_LOG),
    RELAY_2_SHED_TIME(2, SHED_TIME, RELAY_2_SHED_STATUS, RELAY_2_SHED_TIME_DATA_LOG_5_MIN, RELAY_2_SHED_TIME_DATA_LOG_15_MIN, RELAY_2_SHED_TIME_DATA_LOG_30_MIN, RELAY_2_SHED_TIME_DATA_LOG),
    RELAY_3_SHED_TIME(3, SHED_TIME, RELAY_3_SHED_STATUS, RELAY_3_SHED_TIME_DATA_LOG_5_MIN, RELAY_3_SHED_TIME_DATA_LOG_15_MIN, RELAY_3_SHED_TIME_DATA_LOG_30_MIN, RELAY_3_SHED_TIME_DATA_LOG),
    RELAY_4_SHED_TIME(4, SHED_TIME, RELAY_4_SHED_STATUS, RELAY_4_SHED_TIME_DATA_LOG_5_MIN, RELAY_4_SHED_TIME_DATA_LOG_15_MIN, RELAY_4_SHED_TIME_DATA_LOG_30_MIN, RELAY_4_SHED_TIME_DATA_LOG);
    
    enum LogType {
        SHED_TIME,
        RUN_TIME
    }
    
    private int relayNumber;
    private LogType type;
    private BuiltInAttribute relayStatus;
    private Map<BuiltInAttribute, RelayLogInterval> intervalAttributes;

    ItronRelayDataLogs(int relayNumber, LogType type, BuiltInAttribute relay, BuiltInAttribute log5, BuiltInAttribute log15, BuiltInAttribute log30, BuiltInAttribute log60) {
        this.relayNumber = relayNumber;
        this.type = type;
        this.relayStatus = relay;
        this.intervalAttributes = ImmutableMap.of(log5, RelayLogInterval.LOG_5_MINUTE, 
                                                  log15, RelayLogInterval.LOG_15_MINUTE, 
                                                  log30, RelayLogInterval.LOG_30_MINUTE, 
                                                  log60, RelayLogInterval.LOG_60_MINUTE);
    }

    public BuiltInAttribute getRelayStatusAttribute() {
        return relayStatus;
    }
    
    public Map<BuiltInAttribute, RelayLogInterval> getDataLogIntervals() {
        return intervalAttributes;
    }
    
    public boolean isRuntime() {
        return type == RUN_TIME;
    }

    public static Set<BuiltInAttribute> getRelayStatusAttributes() {
        return Arrays.stream(values())
                .map(ItronRelayDataLogs::getRelayStatusAttribute)
                .collect(Collectors.toSet());
    }

    public static Set<BuiltInAttribute> getDataLogAttributes() {
        return Arrays.stream(values())
                .map(ItronRelayDataLogs::getDataLogIntervals)
                .map(Map::keySet)
                .collect(Collectors.reducing(Sets::union))
                .orElse(Collections.emptySet());
    }

    public int getRelayNumber() {
        return relayNumber;
    }
}