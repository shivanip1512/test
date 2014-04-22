package com.cannontech.common.bulk.field;

public enum BulkFieldColumnHeader {
    
    TEMPLATE("template"),
    DEVICE_ID("deviceId"),
    NAME("name"),
    ADDRESS("address"),
    DISCONNECT_ADDRESS("disconnectAddress"),
    METER_NUMBER("meterNumber"),
    ENABLE("enable"),
    ROUTE("route"),
    DEVICE_TYPE("deviceType"),
    RFN_SERIAL_NUMBER("rfnSerialNumber"),
    RFN_MANUFACTURER("rfnManufacturer"),
    RFN_MODEL("rfnModel"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    ;
    
    private String fieldName;
    
    BulkFieldColumnHeader(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public static BulkFieldColumnHeader getForFieldName(String fieldName) {
        for (BulkFieldColumnHeader field : BulkFieldColumnHeader.values()) {
            if (field.fieldName.equals(fieldName)) {
                return field;
            }
        }
        throw new IllegalArgumentException("No BulkFieldColumnHeader found for :" + fieldName);
    }
}