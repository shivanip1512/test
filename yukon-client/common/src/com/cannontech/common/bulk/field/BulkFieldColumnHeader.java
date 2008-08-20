package com.cannontech.common.bulk.field;

public enum BulkFieldColumnHeader {
    
    TEMPLATE("template", false),
    DEVICE_ID("deviceId", false),
    NAME("name", true),
    ADDRESS("address", true),
    DISCONNECT_ADDRESS("disconnectAddress", true),
    METER_NUMBER("meterNumber", true),
    ENABLE("enable", true),
    ROUTE("route", true),
    DEVICE_TYPE("deviceType", false),
    ;
    
    private String fieldName;
    private boolean updateableColumn;
    
    BulkFieldColumnHeader(String fieldName, boolean updateableColumn) {
        this.fieldName = fieldName;
        this.updateableColumn = updateableColumn;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public boolean isUpdateableColumn() {
        return updateableColumn;
    }
}
