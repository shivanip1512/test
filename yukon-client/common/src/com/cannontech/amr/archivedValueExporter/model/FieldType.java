package com.cannontech.amr.archivedValueExporter.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum FieldType implements Displayable{
    METER_NUMBER("Meter Number"),
    DEVICE_NAME("Device Name"),
    DLC_ADDRESS("DLC Address"),
    ROUTE("Route"),
    PLAIN_TEXT("Plain Text"),
    ATTRIBUTE("Attribute");

    /*
        RF_MANUFACTURER("RF Manufacturer"),
        RF_MODEL("RF Model"),
        RF_SERIAL_NUMBER("RF Serial Number"),
        
     */
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