package com.cannontech.dr.rfn.model;

import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RfnLcr6700PointDataMap {
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, FieldType.BLINK_COUNT),
    CONTROL_STATUS(BuiltInAttribute.CONTROL_STATUS, FieldType.CONTROL_STATE),
    RELAY_1_REMAINING_CONTROL(BuiltInAttribute.RELAY_1_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 0),
    RELAY_2_REMAINING_CONTROL(BuiltInAttribute.RELAY_2_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 1),
    RELAY_3_REMAINING_CONTROL(BuiltInAttribute.RELAY_3_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 2),
    SERVICE_STATUS(BuiltInAttribute.SERVICE_STATUS, FieldType.SERVICE_STATE),
    TOTAL_LUF_EVENT(BuiltInAttribute.TOTAL_LUF_COUNT, FieldType.LUF_EVENTS),
    TOTAL_LUV_EVENT(BuiltInAttribute.TOTAL_LUV_COUNT, FieldType.LUV_EVENTS);

    private final BuiltInAttribute attribute;
    private final FieldType fieldType;
    private final Integer relayNum;

    private static final Logger log = YukonLogManager.getLogger(RfnLcr6700PointDataMap.class);
    private static final Set<RfnLcr6700PointDataMap> lcr6700PointDataMap;

    RfnLcr6700PointDataMap(BuiltInAttribute attribute, FieldType fieldType) {
        this(attribute, fieldType, null);
    }

    RfnLcr6700PointDataMap(BuiltInAttribute attribute, FieldType fieldType, Integer relayNum) {
        this.attribute = attribute;
        this.fieldType = fieldType;
        this.relayNum = relayNum;
    }

    static {
        Builder<RfnLcr6700PointDataMap> builder = ImmutableSet.builder();
        builder.add(BLINK_COUNT);
        builder.add(CONTROL_STATUS);
        builder.add(SERVICE_STATUS);
        builder.add(TOTAL_LUF_EVENT);
        builder.add(TOTAL_LUV_EVENT);
        builder.add(RELAY_1_REMAINING_CONTROL);
        builder.add(RELAY_2_REMAINING_CONTROL);
        builder.add(RELAY_3_REMAINING_CONTROL);

        lcr6700PointDataMap = builder.build();
    }

    public static Set<RfnLcr6700PointDataMap> getRelayMapByPaoType(PaoType paoType) {
        if (paoType == PaoType.LCR6700_RFN) {
            return getLcr6700PointDataMap();
        } else {
            log.error("No RFN LCR point mapping data found for pao type: " + paoType.getPaoTypeName());
            throw new IllegalArgumentException();
        }
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public static Set<RfnLcr6700PointDataMap> getLcr6700PointDataMap() {
        return lcr6700PointDataMap;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Integer getRelayNum() {
        return relayNum;
    }

}
