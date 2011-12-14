package com.cannontech.capcontrol.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum IvvcAnalysisScenarioType {

    ABORTED_CBC_COMMS_RATIO_STALE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_REG_COMMS_RATIO_STALE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_STALE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_STALE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_CBC_COMMS_RATIO_INCOMPLETE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_REG_COMMS_RATIO_INCOMPLETE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_ADDITIONAL_VOLTAGE_POINTS_COMMS_RATIO_INCOMPLETE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    ABORTED_ADDITIONAL_REQUIRED_POINTS_COMMS_RATIO_INCOMPLETE(IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT),
    EXECUTED_TAP_RAISE(IvvcAnalysisFormatType.SUBBUS_ID_REG_ID),
    EXECUTED_TAP_LOWER(IvvcAnalysisFormatType.SUBBUS_ID_REG_ID),
    EXECUTED_NO_TAP_MIN_PERIOD(IvvcAnalysisFormatType.SUBBUS_ID_INT),
    EXECUTED_NO_TAP_NEEDED(IvvcAnalysisFormatType.SUBBUS_ID),
    EXECUTED_CAPBANK_OP_CLOSE(IvvcAnalysisFormatType.SUBBUS_ID_CC_PAO_ID),
    EXECUTED_CAPBANK_OP_OPEN(IvvcAnalysisFormatType.SUBBUS_ID_CC_PAO_ID),
    EXECUTED_CAPBANK_OP_ABANDONED_EXCEEDED_MAX_KVAR(IvvcAnalysisFormatType.INT_CC_ID);
    
    private IvvcAnalysisFormatType formatType;
    
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

    private IvvcAnalysisScenarioType(IvvcAnalysisFormatType formatType) {
        this.formatType = formatType;
    }

    public IvvcAnalysisFormatType getFormatType() {
        return formatType;
    }

    public static IvvcAnalysisScenarioType getEnumWithId(int scenarioId) {
        return scenarioIdMap.get(scenarioId);
    }
}
