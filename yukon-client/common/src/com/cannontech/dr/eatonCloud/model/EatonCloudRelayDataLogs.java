package com.cannontech.dr.eatonCloud.model;

import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.*;


import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.dr.service.RelayLogInterval;
import com.cannontech.dr.service.RuntimeCalcSchedulerService.LogType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public enum EatonCloudRelayDataLogs {

    RELAY_1_RUN_TIME(1, LogType.RUN_TIME, RELAY_1_LOAD_STATE, RELAY_1_RUN_TIME_DATA_LOG_5_MIN, RELAY_1_RUN_TIME_DATA_LOG_15_MIN, RELAY_1_RUN_TIME_DATA_LOG_30_MIN),
    RELAY_2_RUN_TIME(2, LogType.RUN_TIME, RELAY_2_LOAD_STATE, RELAY_2_RUN_TIME_DATA_LOG_5_MIN, RELAY_2_RUN_TIME_DATA_LOG_15_MIN, RELAY_2_RUN_TIME_DATA_LOG_30_MIN),
    RELAY_3_RUN_TIME(3, LogType.RUN_TIME, RELAY_3_LOAD_STATE, RELAY_3_RUN_TIME_DATA_LOG_5_MIN, RELAY_3_RUN_TIME_DATA_LOG_15_MIN, RELAY_3_RUN_TIME_DATA_LOG_30_MIN),
    RELAY_4_RUN_TIME(4, LogType.RUN_TIME, RELAY_4_LOAD_STATE, RELAY_4_RUN_TIME_DATA_LOG_5_MIN, RELAY_4_RUN_TIME_DATA_LOG_15_MIN, RELAY_4_RUN_TIME_DATA_LOG_30_MIN),

    RELAY_1_SHED_TIME(1, LogType.SHED_TIME, RELAY_1_ACTIVATION_STATUS, RELAY_1_SHED_TIME_DATA_LOG_5_MIN, RELAY_1_SHED_TIME_DATA_LOG_15_MIN, RELAY_1_SHED_TIME_DATA_LOG_30_MIN),
    RELAY_2_SHED_TIME(2, LogType.SHED_TIME, RELAY_2_ACTIVATION_STATUS, RELAY_2_SHED_TIME_DATA_LOG_5_MIN, RELAY_2_SHED_TIME_DATA_LOG_15_MIN, RELAY_2_SHED_TIME_DATA_LOG_30_MIN),
    RELAY_3_SHED_TIME(3, LogType.SHED_TIME, RELAY_3_ACTIVATION_STATUS, RELAY_3_SHED_TIME_DATA_LOG_5_MIN, RELAY_3_SHED_TIME_DATA_LOG_15_MIN, RELAY_3_SHED_TIME_DATA_LOG_30_MIN),
    RELAY_4_SHED_TIME(4, LogType.SHED_TIME, RELAY_4_ACTIVATION_STATUS, RELAY_4_SHED_TIME_DATA_LOG_5_MIN, RELAY_4_SHED_TIME_DATA_LOG_15_MIN, RELAY_4_SHED_TIME_DATA_LOG_30_MIN);
    

    private int relayNumber;
    private LogType type;
    private BuiltInAttribute relayStatus;
    private Map<BuiltInAttribute, RelayLogInterval> intervalAttributes;

    EatonCloudRelayDataLogs(int relayNumber, LogType type, BuiltInAttribute relay, BuiltInAttribute log5, BuiltInAttribute log15, BuiltInAttribute log30) {
        this.relayNumber = relayNumber;
        this.type = type;
        this.relayStatus = relay;
        this.intervalAttributes = ImmutableMap.of(log5, RelayLogInterval.LOG_5_MINUTE, 
                                                  log15, RelayLogInterval.LOG_15_MINUTE, 
                                                  log30, RelayLogInterval.LOG_30_MINUTE);
    }

    public BuiltInAttribute getRelayStatusAttribute() {
        return relayStatus;
    }
    
    public Map<BuiltInAttribute, RelayLogInterval> getDataLogIntervals() {
        return intervalAttributes;
    }
    
    public boolean isRuntime() {
        return type == LogType.RUN_TIME;
    }

    public static Set<BuiltInAttribute> getRelayStatusAttributes() {
        return Arrays.stream(values())
                .map(EatonCloudRelayDataLogs::getRelayStatusAttribute)
                .collect(Collectors.toSet());
    }

    public static Set<BuiltInAttribute> getDataLogAttributes() {
        return Arrays.stream(values())
                .map(EatonCloudRelayDataLogs::getDataLogIntervals)
                .map(Map::keySet)
                .collect(Collectors.reducing(Sets::union))
                .orElse(Collections.emptySet());
    }

    public int getRelayNumber() {
        return relayNumber;
    }
}