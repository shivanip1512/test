package com.cannontech.common.bulk.field;

import java.util.Comparator;

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
    
    public final static Comparator<BulkFieldColumnHeader> FIELD_NAME_COMPARATOR =
            new Comparator<BulkFieldColumnHeader>() {
                @Override
                public int compare(BulkFieldColumnHeader o1, BulkFieldColumnHeader o2) {
                    return o1.getFieldName().compareTo(o2.getFieldName());
                }
            };
    
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