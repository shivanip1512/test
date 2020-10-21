package com.cannontech.capcontrol.creation;

import java.util.List;
import java.util.Locale;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.types.ZoneImportType;
import com.cannontech.database.data.pao.ZoneType;
import com.google.common.collect.Lists;

public enum CapControlIvvcZoneImportField {
    TYPE(InputType.REQUIRED, "TYPE", ZoneImportType.class, false, true),
    ACTION(InputType.REQUIRED, "ACTION", ImportAction.class, false, true),
    ZONE_NAME(InputType.REQUIRED, "ZONE NAME", String.class, false, true),
    ZONE_TYPE(InputType.VALUE_DEPENDENT, "ZONE TYPE", ZoneType.class, false, true, "TYPE", ZoneImportType.ZONE),
    PARENT_ZONE(InputType.VALUE_DEPENDENT, "PARENT ZONE", String.class, false, true, "TYPE", ZoneImportType.ZONE),
    SUBSTATION_BUS(InputType.VALUE_DEPENDENT, "SUBSTATION BUS", String.class, false, true, "PARENT ZONE", "ROOT"),
    GRAPH_START_POSITION(InputType.VALUE_DEPENDENT, "GRAPH START POSITION", String.class, false, true, "TYPE", ZoneImportType.ZONE),
    POSITION(InputType.VALUE_DEPENDENT, "POSITION", String.class, false, true, "TYPE", ZoneImportType.CAP_BANK, ZoneImportType.VOLTAGE_POINT),
    DISTANCE(InputType.VALUE_DEPENDENT, "DISTANCE", String.class, false, true, "TYPE", ZoneImportType.CAP_BANK, ZoneImportType.VOLTAGE_POINT),
    VOLTAGE_POINT_NAME(InputType.VALUE_DEPENDENT, "VOLTAGE POINT NAME", String.class, false, true, "TYPE", ZoneImportType.VOLTAGE_POINT),
    DEVICE_NAME(InputType.VALUE_DEPENDENT, "DEVICE NAME", String.class, false, true, "TYPE", ZoneImportType.CAP_BANK, ZoneImportType.VOLTAGE_POINT, ZoneImportType.REGULATOR),
    PHASE(InputType.VALUE_DEPENDENT, "PHASE", String.class, false, true, "TYPE", ZoneImportType.VOLTAGE_POINT, ZoneImportType.REGULATOR),
    IGNORE(InputType.VALUE_DEPENDENT, "IGNORE", String.class, false, true, "TYPE", ZoneImportType.VOLTAGE_POINT),
    ;

    public enum InputType {
        REQUIRED, VALUE_DEPENDENT, OPTIONAL
    }

    private InputType inputType;
    private String name;
    private Class<?> typeClass;
    private boolean nullable;
    private boolean uppercaseValue;
    private String dependedColumnName;
    private Object[] dependedColumnValues;

    CapControlIvvcZoneImportField(InputType inputType, String name, Class<?> typeClass) {
        this(inputType, name, typeClass, false, false);
    }

    CapControlIvvcZoneImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable,
                                  boolean uppercaseValue) {
        this(inputType, name, typeClass, nullable, uppercaseValue, null);
    }

    CapControlIvvcZoneImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable,
                                  boolean uppercaseValue,
                                  String dependedColumnName, Object... dependentValues) {
        this.inputType = inputType;
        this.name = name;
        this.typeClass = typeClass;
        this.nullable = nullable;
        this.uppercaseValue = uppercaseValue;
        this.dependedColumnName = dependedColumnName;
        this.dependedColumnValues = dependentValues;
    }

    public InputType getInputType() {
        return inputType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUppercaseValue() {
        return uppercaseValue;
    }

    public String getDependedColumnName() {
        return dependedColumnName;
    }

    public Object[] getDependedColumnValues() {
        return dependedColumnValues;
    }

    public static List<String> getRequiredFieldNames() {
        List<String> requiredFields = Lists.newArrayList();
        for (CapControlIvvcZoneImportField format : values()) {
            if (format.getInputType() == InputType.REQUIRED) {
                requiredFields.add(format.getName());

            }
        }
        return requiredFields;
    }

    public static List<String> getValueDependentFieldNames() {
        List<String> valueDependentFields = Lists.newArrayList();
        for (CapControlIvvcZoneImportField format : values()) {
            if (format.getInputType() == InputType.VALUE_DEPENDENT) {
                valueDependentFields.add(format.getName());
            }
        }
        return valueDependentFields;
    }

    public static List<String> getOptionalFieldNames() {
        List<String> optionalFields = Lists.newArrayList();
        for (CapControlIvvcZoneImportField format : values()) {
            if (format.getInputType() == InputType.OPTIONAL) {
                optionalFields.add(format.getName());
            }
        }
        return optionalFields;
    }

    public static CapControlIvvcZoneImportField getByFieldName(String field) {
        return valueOf(field.replaceAll(" ", "_").toUpperCase(Locale.US));
    }
}
