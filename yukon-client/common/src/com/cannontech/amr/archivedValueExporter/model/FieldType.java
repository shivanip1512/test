package com.cannontech.amr.archivedValueExporter.model;

import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableSet;

public enum FieldType implements Displayable{
    METER_NUMBER("Meter Number"),
    DEVICE_NAME("Device Name"),
    ADDRESS("Address/Serial Number"),
    ROUTE("Route"),
    PLAIN_TEXT("Plain Text"),
    ATTRIBUTE("Attribute"),
    ATTRIBUTE_NAME("Attribute Name"),
    UNIT_OF_MEASURE("Unit of Measure"),
    POINT_NAME("Point Name"),
    POINT_VALUE("Value"),
    POINT_TIMESTAMP("Timestamp"),
    POINT_QUALITY("Quality"),
    ;

    /*
        RF_MANUFACTURER("RF Manufacturer"),
        RF_MODEL("RF Model"),
        RF_SERIAL_NUMBER("RF Serial Number"),
     */
    
    public static final Set<FieldType> FIXED_ATTRIBUTE_FIELD_TYPES = 
            ImmutableSet.of(ADDRESS, ATTRIBUTE, DEVICE_NAME, METER_NUMBER, PLAIN_TEXT, ROUTE); 
    public static final Set<FieldType> DYNAMIC_ATTRIBUTE_FIELD_TYPES = 
            ImmutableSet.of(ADDRESS, ATTRIBUTE_NAME, DEVICE_NAME, METER_NUMBER, PLAIN_TEXT, POINT_NAME, ROUTE, POINT_TIMESTAMP, UNIT_OF_MEASURE, POINT_VALUE, POINT_QUALITY); 
    
    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.fieldType.";
    
    private String description;

    private FieldType(String description) {
        this.setDescription(description);
    }

    public String getKey() {
        return name();
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault( keyPrefix  + name(), description);
    }
}