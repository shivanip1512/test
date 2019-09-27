package com.cannontech.dr.rfn.model;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.google.common.collect.ImmutableSet;

public enum RfnLcrTlvPointDataType {
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, FieldType.BLINK_COUNT),
    CONTROL_STATUS(BuiltInAttribute.CONTROL_STATUS, FieldType.CONTROL_STATE),
    RELAY_1_REMAINING_CONTROL(BuiltInAttribute.RELAY_1_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 0),
    RELAY_2_REMAINING_CONTROL(BuiltInAttribute.RELAY_2_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 1),
    RELAY_3_REMAINING_CONTROL(BuiltInAttribute.RELAY_3_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 2),
    RELAY_4_REMAINING_CONTROL(BuiltInAttribute.RELAY_4_REMAINING_CONTROL, FieldType.RELAY_N_REMAINING_CONTROLTIME, 3),
    SERVICE_STATUS(BuiltInAttribute.SERVICE_STATUS, FieldType.SERVICE_STATE),
    TOTAL_LUF_EVENT(BuiltInAttribute.TOTAL_LUF_COUNT, FieldType.LUF_EVENTS),
    TOTAL_LUV_EVENT(BuiltInAttribute.TOTAL_LUV_COUNT, FieldType.LUV_EVENTS),
    LOV_TRIGGER(BuiltInAttribute.LOV_TRIGGER, FieldType.LOV_TRIGGER, true),
    LOV_RESTORE(BuiltInAttribute.LOV_RESTORE, FieldType.LOV_RESTORE, true),
    LOV_TRIGGER_TIME(BuiltInAttribute.LOV_TRIGGER_TIME, FieldType.LOV_TRIGGER_TIME, true),
    LOV_RESTORE_TIME(BuiltInAttribute.LOV_RESTORE_TIME, FieldType.LOV_RESTORE_TIME, true),
    LOV_EVENT_COUNT(BuiltInAttribute.TOTAL_LOV_COUNT, FieldType.LOV_EVENT_COUNT, true),
    LOF_TRIGGER(BuiltInAttribute.LOF_TRIGGER, FieldType.LOF_TRIGGER, true),
    LOF_RESTORE(BuiltInAttribute.LOF_RESTORE, FieldType.LOF_RESTORE, true),
    LOF_TRIGGER_TIME(BuiltInAttribute.LOF_TRIGGER_TIME, FieldType.LOF_TRIGGER_TIME, true),
    LOF_RESTORE_TIME(BuiltInAttribute.LOF_RESTORE_TIME, FieldType.LOF_RESTORE_TIME, true),
    LOF_EVENT_COUNT(BuiltInAttribute.TOTAL_LOF_COUNT, FieldType.LOF_EVENT_COUNT, true),
    LOF_START_RANDOM_TIME(BuiltInAttribute.LOF_START_RANDOM_TIME, FieldType.LOF_START_RANDOM_TIME, true),
    LOF_END_RANDOM_TIME(BuiltInAttribute.LOF_END_RANDOM_TIME, FieldType.LOF_END_RANDOM_TIME, true),
    LOV_START_RANDOM_TIME(BuiltInAttribute.LOV_START_RANDOM_TIME, FieldType.LOV_START_RANDOM_TIME, true),
    LOV_END_RANDOM_TIME(BuiltInAttribute.LOV_END_RANDOM_TIME, FieldType.LOV_END_RANDOM_TIME, true),
    LOF_MIN_EVENT_DURATION(BuiltInAttribute.LOF_MIN_EVENT_DURATION, FieldType.LOF_MIN_EVENT_DURATION, true),
    LOV_MIN_EVENT_DURATION(BuiltInAttribute.LOV_MIN_EVENT_DURATION, FieldType.LOV_MIN_EVENT_DURATION, true),
    LOF_MAX_EVENT_DURATION(BuiltInAttribute.LOF_MAX_EVENT_DURATION, FieldType.LOF_MAX_EVENT_DURATION, true),
    LOV_MAX_EVENT_DURATION(BuiltInAttribute.LOV_MAX_EVENT_DURATION, FieldType.LOV_MAX_EVENT_DURATION, true),
    MINIMUM_EVENT_SEPARATION(BuiltInAttribute.MINIMUM_EVENT_SEPARATION, FieldType.MINIMUM_EVENT_SEPARATION, true),
    POWER_QUALITY_RESPONSE_ENABLED(BuiltInAttribute.POWER_QUALITY_RESPONSE_ENABLED, FieldType.POWER_QUALITY_RESPONSE_ENABLED, true)
    ;

    private final BuiltInAttribute attribute;
    private final FieldType fieldType;
    private final Integer relayNum;
    private final boolean isPowerQualityResponse;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrTlvPointDataType.class);
    private static final Set<RfnLcrTlvPointDataType> lcr6700PointDataTypes = ImmutableSet.copyOf(values());
    
    RfnLcrTlvPointDataType(BuiltInAttribute attribute, FieldType fieldType) {
        this(attribute, fieldType, null);
    }

    RfnLcrTlvPointDataType(BuiltInAttribute attribute, FieldType fieldType, Integer relayNum) {
        this.attribute = attribute;
        this.fieldType = fieldType;
        this.relayNum = relayNum;
        isPowerQualityResponse = false;
    }
    
    RfnLcrTlvPointDataType(BuiltInAttribute attribute, FieldType fieldType, boolean isPowerQualityResponse) {
        this.attribute = attribute;
        this.fieldType = fieldType;
        relayNum = null;
        this.isPowerQualityResponse = isPowerQualityResponse;
    }

    public static Set<RfnLcrTlvPointDataType> getPointDataTypesByPaoType(PaoType paoType) {
        if (paoType == PaoType.LCR6700_RFN) {
            return lcr6700PointDataTypes;
        }
        log.error("No RFN LCR point data types found for pao type: " + paoType.getPaoTypeName());
        throw new IllegalArgumentException();
    }

    public boolean isPowerQualityResponse() {
        return isPowerQualityResponse;
    }
    
    public boolean isRelayData() {
        return attribute.getAttributeGroup() == AttributeGroup.RELAY;
    }
    
    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Integer getRelayNum() {
        return relayNum;
    }

}
