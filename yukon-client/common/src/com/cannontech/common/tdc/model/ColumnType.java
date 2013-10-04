package com.cannontech.common.tdc.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ColumnType {
    
    //select * from columntype;
    POINT_ID(1, "Point ID"),
    POINT_NAME(2, "Point Name"),
    POINT_TYPE(3, "Point Type"),
    POINT_STATE(4, "Point State"),
    DEVICE_NAME(5, "Device Name"),
    DEVICE_TYPE(6, "Device Type"),
    DEVICE_CURRENT_STATE(7, "Device Current State"),
    DEVICE_ID(8, "Device ID"),
    POINT_VALUE(9, "Point Value"),
    POINT_QUALITY(10, "Point Quality"),
    POINT_TIME_STAMP(11, "Point Timestamp"),
    U_OF_M(12, "UofM"),
    STATE(13, "State"),
    
    // the columns below are not defined in columntype. 
    TIME_STAMP(-1, "Time Stamp"),
    TEXT_MESSAGE(-2, "Text Message"),
    USERNAME(-3, "User Name"),
    ADDITIONAL_INFO(-4, "Additional Info"),
    DESCRIPTION(-5, "Description"),
    TAG(-6, "Tag");
    
    private final static Map<String, ColumnType> lookupByName;
    private final static Map<Integer, ColumnType> lookupByTypeId;
    static {
        Builder<Integer, ColumnType> byTypeIdBuilder =
            ImmutableMap.builder();

        for (ColumnType columnType : values()) {
            byTypeIdBuilder.put(columnType.getTypeId(), columnType);
        }
        lookupByTypeId = byTypeIdBuilder.build();
        Builder<String, ColumnType> lookupByNameBuilder =
            ImmutableMap.builder();

        for (ColumnType columnType : values()) {
            lookupByNameBuilder.put(columnType.getColumnName(), columnType);
        }
        lookupByName = lookupByNameBuilder.build();
    }

    public static ColumnType getByTypeId(Integer typeId) {
        return lookupByTypeId.get(typeId);
    }
    
    public static ColumnType getByName(String name) {
        return lookupByName.get(name);
    }

    private int typeId;
    private String name;

    ColumnType(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    public String getColumnName() {
        return name;
    }

    public int getTypeId() {
        return typeId;
    }
}
