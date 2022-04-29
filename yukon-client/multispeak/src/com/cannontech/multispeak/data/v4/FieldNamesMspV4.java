package com.cannontech.multispeak.data.v4;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;

public enum FieldNamesMspV4 {

    PEAK_DEMAND(BuiltInAttribute.PEAK_DEMAND, "maxDemand"),
    USAGE(BuiltInAttribute.USAGE, "posKWh"),
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, "outageCount");

    private String fieldName;
    private BuiltInAttribute attribute;

    private FieldNamesMspV4(BuiltInAttribute attribute, String fieldName) {
        this.fieldName = fieldName;
        this.attribute = attribute;
    }

    private static final Logger log = YukonLogManager.getLogger(FieldNamesMspV4.class);
    static ImmutableMap<BuiltInAttribute, FieldNamesMspV4> fieldNameByAttribute;
    static {
        try {
            ImmutableMap.Builder<BuiltInAttribute, FieldNamesMspV4> builder = ImmutableMap.builder();
            for (FieldNamesMspV4 fieldNamesMspV4 : values()) {
                builder.put(fieldNamesMspV4.attribute, fieldNamesMspV4);
            }
            fieldNameByAttribute = builder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building fieldName Map", e);
            throw e;
        }
    }

    public static FieldNamesMspV4 getFieldNamesMspV4ByAttribute(BuiltInAttribute attribute) {
        return fieldNameByAttribute.get(attribute);
    }

    public String getFieldName() {
        return fieldName;
    }

}
