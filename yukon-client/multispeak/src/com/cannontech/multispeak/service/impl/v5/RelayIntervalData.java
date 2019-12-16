package com.cannontech.multispeak.service.impl.v5;

import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.dr.service.RelayLogInterval;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum RelayIntervalData {

    RUNTIMERELAY_1("RunTime", 1, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_5_MIN,
                                        BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_15_MIN,
                                        BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG_30_MIN,
                                        BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG),

    SHEDTIMERELAY_1("ShedTime", 1, BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG_5_MIN, 
                                          BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG_15_MIN,
                                          BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG_30_MIN, 
                                          BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG),

    RUNTIMERELAY_2("RunTime", 2, BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG_5_MIN, 
                                        BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG_15_MIN,
                                        BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG_30_MIN, 
                                        BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG),

    SHEDTIMERELAY_2("ShedTime", 2, BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG_5_MIN, 
                                          BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG_15_MIN,
                                          BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG_30_MIN, 
                                          BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG),

    RUNTIMERELAY_3("RunTime", 3, BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG_5_MIN, 
                                        BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG_15_MIN,
                                        BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG_30_MIN, 
                                        BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG),

    SHEDTIMERELAY_3("ShedTime", 3, BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG_5_MIN, 
                                          BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG_15_MIN,
                                          BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG_30_MIN, 
                                          BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG),

    RUNTIMERELAY_4("RunTime", 4, BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG_5_MIN, 
                                        BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG_15_MIN,
                                        BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG_30_MIN, 
                                        BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG),

    SHEDTIMERELAY_4("ShedTime", 4, BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG_5_MIN, 
                                          BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG_15_MIN,
                                          BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG_30_MIN, 
                                          BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG);

    private ImmutableSet<BuiltInAttribute> attributes;
    private String relayDataReadingTypeCodeString;
    private Integer relayNumber;
    private Map<BuiltInAttribute, RelayLogInterval> intervalAttributes;

    private RelayIntervalData(String relayDataReadingTypeCodeString, Integer relayNumber, BuiltInAttribute...attributes) {
        this.attributes = ImmutableSet.copyOf(attributes);
        this.relayDataReadingTypeCodeString = relayDataReadingTypeCodeString;
        this.relayNumber = relayNumber;

        this.intervalAttributes = ImmutableMap.of(attributes[0], RelayLogInterval.LOG_5_MINUTE,
                                                  attributes[1], RelayLogInterval.LOG_15_MINUTE,
                                                  attributes[2], RelayLogInterval.LOG_30_MINUTE,
                                                  attributes[3], RelayLogInterval.LOG_60_MINUTE);
    }

    private static ImmutableMap<BuiltInAttribute, RelayIntervalData> lookupByAttribute;

    static {
        ImmutableMap.Builder<BuiltInAttribute, RelayIntervalData> idBuilder = ImmutableMap.builder();
        for (RelayIntervalData readingTypeCode : values()) {
            for (BuiltInAttribute attribute : readingTypeCode.attributes) {
                idBuilder.put(attribute, readingTypeCode);
            }
        }
        lookupByAttribute = idBuilder.build();

    }

    public static RelayIntervalData getRelayIntervalData(BuiltInAttribute attribute) {
        return lookupByAttribute.get(attribute);

    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public String getRelayDataReadingTypeCodeString() {
        return relayDataReadingTypeCodeString;
    }

    public Map<BuiltInAttribute, RelayLogInterval> getIntervalAttributes() {
        return intervalAttributes;
    }

    public RelayLogInterval getRelayLogInterval(BuiltInAttribute attribute) {
        return intervalAttributes.get(attribute);

    }

}
