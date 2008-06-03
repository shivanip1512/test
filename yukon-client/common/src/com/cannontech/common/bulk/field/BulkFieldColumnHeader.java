package com.cannontech.common.bulk.field;

public enum BulkFieldColumnHeader {
    
    TEMPLATE("template"),
    DEVICE_ID("deviceId"),
    NAME("name"),
    ADDRESS("address"),
    METER_NUMBER("meterNumber"),
    ENABLE("enable"),
    ROUTE("route"),
    DEVICE_TYPE("deviceType"),
    ;
    
    private String fieldName;
    
    BulkFieldColumnHeader(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
