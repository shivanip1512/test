package com.cannontech.amr.archivedValueExporter.model;

import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableSet;

public enum FieldType implements Displayable{
    METER_NUMBER("Meter Number", OptionalField.METER_NUMBER),
    DEVICE_NAME("Device Name", OptionalField.NAME),
    DEVICE_TYPE("Device Type", OptionalField.TYPE),
    ADDRESS("Address/Serial Number", OptionalField.ADDRESS_OR_SERIAL_NUMBER),
    ROUTE("Route", OptionalField.ROUTE_NAME),
    PLAIN_TEXT("Plain Text"),
    RUNTIME("Time of Export"),
    ATTRIBUTE("Attribute"),
    ATTRIBUTE_NAME("Attribute Name"),
    UNIT_OF_MEASURE("Unit of Measure"),
    POINT_NAME("Point Name"),
    POINT_VALUE("Value"),
    POINT_TIMESTAMP("Timestamp"),
    POINT_QUALITY("Quality"),
    POINT_STATE("State"),
    ;

    /*
        RF_MANUFACTURER("RF Manufacturer"),
        RF_MODEL("RF Model"),
        RF_SERIAL_NUMBER("RF Serial Number"),
     */
    
    public static final Set<FieldType> FIXED_ATTRIBUTE_FIELD_TYPES = 
            ImmutableSet.of(ADDRESS, ATTRIBUTE, DEVICE_NAME, DEVICE_TYPE, METER_NUMBER, PLAIN_TEXT, ROUTE, RUNTIME); 
    public static final Set<FieldType> DYNAMIC_ATTRIBUTE_FIELD_TYPES = 
            ImmutableSet.of(ADDRESS, ATTRIBUTE_NAME, DEVICE_NAME, DEVICE_TYPE, METER_NUMBER, PLAIN_TEXT, POINT_NAME, ROUTE, POINT_STATE, POINT_TIMESTAMP, RUNTIME, UNIT_OF_MEASURE, POINT_VALUE, POINT_QUALITY); 
    
    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.fieldType.";
    
    private final String description;
    private final OptionalField paoDataOptionalField;

    private FieldType(String description, OptionalField paoDataOptionalField) {
        this.description = description;
        this.paoDataOptionalField = paoDataOptionalField;
    }

    private FieldType(String description) {
        this(description, null);
    }

    public String getKey() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    /**
     * If this field type gets its data from an optional field in a {@link PaoData} object, this will return that
     * optional field.
     */
    public OptionalField getPaoDataOptionalField() {
        return paoDataOptionalField;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault( keyPrefix  + name(), description);
    }
}
