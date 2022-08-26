package com.cannontech.multispeak.data.v4;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;
import com.google.common.collect.ImmutableMap;

public enum FieldNamesMspV4 {

    PEAK_DEMAND(BuiltInAttribute.PEAK_DEMAND, SyntaxItem.PEAK_DEMAND),
    USAGE(BuiltInAttribute.USAGE, SyntaxItem.KWH),
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, SyntaxItem.BLINK_COUNT),
    KVA(BuiltInAttribute.KVA, SyntaxItem.KVA),
    KVAR(BuiltInAttribute.KVAR, SyntaxItem.KVAR),
    KVARH(BuiltInAttribute.KVARH, SyntaxItem.KVARH),
    DELIVERED_KWH_RATE_A(BuiltInAttribute.DELIVERED_KWH_RATE_A, SyntaxItem.DELIVERED_KWH_RATE_A),
    DELIVERED_KWH_RATE_B(BuiltInAttribute.DELIVERED_KWH_RATE_B, SyntaxItem.DELIVERED_KWH_RATE_B),
    DELIVERED_KWH_RATE_C(BuiltInAttribute.DELIVERED_KWH_RATE_C, SyntaxItem.DELIVERED_KWH_RATE_C),
    DELIVERED_KWH_RATE_D(BuiltInAttribute.DELIVERED_KWH_RATE_D, SyntaxItem.DELIVERED_KWH_RATE_D),
    RECEIVED_KWH_RATE_A(BuiltInAttribute.RECEIVED_KWH_RATE_A, SyntaxItem.RECEIVED_KWH_RATE_A),
    RECEIVED_KWH_RATE_B(BuiltInAttribute.RECEIVED_KWH_RATE_B, SyntaxItem.RECEIVED_KWH_RATE_B),
    RECEIVED_KWH_RATE_C(BuiltInAttribute.RECEIVED_KWH_RATE_C, SyntaxItem.RECEIVED_KWH_RATE_C),
    RECEIVED_KWH_RATE_D(BuiltInAttribute.RECEIVED_KWH_RATE_D, SyntaxItem.RECEIVED_KWH_RATE_D),
    PEAK_DEMAND_RATE_A(BuiltInAttribute.PEAK_DEMAND_RATE_A, SyntaxItem.PEAK_DEMAND_RATE_A),
    PEAK_DEMAND_RATE_B(BuiltInAttribute.PEAK_DEMAND_RATE_B, SyntaxItem.PEAK_DEMAND_RATE_B),
    PEAK_DEMAND_RATE_C(BuiltInAttribute.PEAK_DEMAND_RATE_C, SyntaxItem.PEAK_DEMAND_RATE_C),
    PEAK_DEMAND_RATE_D(BuiltInAttribute.PEAK_DEMAND_RATE_D, SyntaxItem.PEAK_DEMAND_RATE_D),
    RECEIVED_KWH(BuiltInAttribute.RECEIVED_KWH, SyntaxItem.RECEIVED_KWH),
    POWER_FACTOR(BuiltInAttribute.POWER_FACTOR, SyntaxItem.POWER_FACTOR);

    private SyntaxItem syntaxItem;
    private BuiltInAttribute attribute;

    private FieldNamesMspV4(BuiltInAttribute attribute, SyntaxItem syntaxItem) {
        this.syntaxItem = syntaxItem;
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
        return syntaxItem.getMspFieldName();
    }

}
