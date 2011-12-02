package com.cannontech.capcontrol.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum IvvcAnalysisScenarioType {

    ABORTED_CBC_COMMS_RATIO_STALE(1),
    ABORTED_REG_COMMS_RATIO_STALE(1),
    ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_STALE(1),
    ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_STALE(1),
    ABORTED_CBC_COMMS_RATIO_INCOMPLETE(1),
    ABORTED_REG_COMMS_RATIO_INCOMPLETE(1),
    ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_INCOMPLETE(1),
    ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_INCOMPLETE(1),
    EXECUTED_TAP_RAISE(2),
    EXECUTED_TAP_LOWER(2),
    EXECUTED_NO_TAP_MIN_PERIOD(3),
    EXECUTED_NO_TAP_NEEDED(4),
    EXECUTED_CAPBANK_OP_CLOSE(5),
    EXECUTED_CAPBANK_OP_OPEN(5),
    EXECUTED_CAPBANK_OP_ABANDONED_EXCEEDED_MAX_KVAR(6);
    
    private int formatType;
    
    private static ImmutableMap<Integer, IvvcAnalysisScenarioType> scenarioIdMap;
    static {
        // Integer key = scenarioId, which CapControl (IVVC) is sending us
        Builder<Integer, IvvcAnalysisScenarioType> builder = ImmutableMap.builder();
        builder.put(1, ABORTED_CBC_COMMS_RATIO_STALE);
        builder.put(2, ABORTED_REG_COMMS_RATIO_STALE);
        builder.put(3, ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_STALE);
        builder.put(4, ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_STALE);
        builder.put(5, ABORTED_CBC_COMMS_RATIO_INCOMPLETE);
        builder.put(6, ABORTED_REG_COMMS_RATIO_INCOMPLETE);
        builder.put(7, ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_INCOMPLETE);
        builder.put(8, ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_INCOMPLETE);
        builder.put(9, EXECUTED_TAP_RAISE);
        builder.put(10, EXECUTED_TAP_LOWER);
        builder.put(11, EXECUTED_NO_TAP_MIN_PERIOD);
        builder.put(12, EXECUTED_NO_TAP_NEEDED);
        builder.put(13, EXECUTED_CAPBANK_OP_CLOSE);
        builder.put(14, EXECUTED_CAPBANK_OP_OPEN);
        builder.put(15, EXECUTED_CAPBANK_OP_ABANDONED_EXCEEDED_MAX_KVAR);
        
        scenarioIdMap = builder.build();
    }

    private IvvcAnalysisScenarioType(int formatType) {
        this.formatType = formatType;
    }

    public int getFormatType() {
        return formatType;
    }

    public static IvvcAnalysisScenarioType getEnumWithId(int scenarioId) {
        return scenarioIdMap.get(scenarioId);
    }
}
