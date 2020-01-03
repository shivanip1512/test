package com.cannontech.multispeak.service.impl.v5;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.google.common.collect.ImmutableMap;

public enum VoltageFieldKindName {

    MAXIMUM_VOLTAGE("MaxVoltage", FieldNameKind.MAX_VOLTAGE, BuiltInAttribute.MAXIMUM_VOLTAGE), 
    MINIMUM_VOLTAGE("MinVoltage", FieldNameKind.MIN_VOLTAGE, BuiltInAttribute.MINIMUM_VOLTAGE), 
    AVERAGE_VOLTAGE("AverageVoltage", FieldNameKind.OTHER, BuiltInAttribute.AVERAGE_VOLTAGE);

    private String fieldName;
    private FieldNameKind fieldNameKind;
    private BuiltInAttribute attribute;

    private VoltageFieldKindName(String fieldName, FieldNameKind fieldNameKind, BuiltInAttribute attribute) {
        this.fieldName = fieldName;
        this.fieldNameKind = fieldNameKind;
        this.attribute = attribute;
    }

    private static ImmutableMap<BuiltInAttribute, VoltageFieldKindName> lookupByAttribute;

    static {
        ImmutableMap.Builder<BuiltInAttribute, VoltageFieldKindName> idBuilder = ImmutableMap.builder();
        for (VoltageFieldKindName voltageFieldlKindName : values()) {
            idBuilder.put(voltageFieldlKindName.attribute, voltageFieldlKindName);

        }
        lookupByAttribute = idBuilder.build();

    }

    public static VoltageFieldKindName getVoltageFieldKindNameData(BuiltInAttribute attribute) {
        return lookupByAttribute.get(attribute);

    }

    public FieldNameKind getfieldNameKind() {
        return fieldNameKind;
    }

    public String getfieldName() {
        return fieldName;
    }
}
