package com.cannontech.amr.archivedValueExporter.model;

public enum FieldType {
    METER_NUMBER("Meter Number"),
    DEVICE_NAME("Device Name"),
    DLC_ADDRESS("DLC Address"),
    RF_MANUFACTURER("RF Manufacturer"),
    RF_MODEL("RF Model"),
    RF_SERIAL_NUMBER("RF Serial Number"),
    PLAIN_TEXT("Plain Text"),
    ATTRIBUTE("Attribute");

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
}