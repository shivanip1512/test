package com.cannontech.common.tdc.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ColumnType {
    
    //select * from columntype;
    POINT_ID(1, "Point ID", 0),
    POINT_NAME(2, "Point Name", 100),
    POINT_TYPE(3, "Point Type", 0),
    POINT_STATE(4, "Point State", 0),
    DEVICE_NAME(5, "Device Name", 100),
    DEVICE_TYPE(6, "Device Type", 10),
    DEVICE_CURRENT_STATE(7, "Device Current State", 0),
    DEVICE_ID(8, "Device ID", 0),
    POINT_VALUE(9, "Point Value", 10),
    POINT_QUALITY(10, "Point Quality", 10),
    POINT_TIME_STAMP(11, "Point Timestamp", 10),
    U_OF_M(12, "UofM", 10),
    STATE(13, "State", 10),
    
    // the columns below are not defined in columntype. 
    TIME_STAMP(-1, "Timestamp", 0),
    TEXT_MESSAGE(-2, "Text Message", 0),
    USERNAME(-3, "User Name", 0),
    ADDITIONAL_INFO(-4, "Additional Info", 0),
    DESCRIPTION(-5, "Description", 0),
    TAG(-6, "Tag", 0);
    
    private final static Map<String, ColumnType> lookupByName;
    private final static Map<Integer, ColumnType> lookupByTypeId;
    private final static List<ColumnType> defaultColumns;
    
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
        
        //columns that can be created by Web TDC
        defaultColumns = Collections.unmodifiableList(
            Arrays.asList(ColumnType.DEVICE_NAME, ColumnType.DEVICE_TYPE, ColumnType.POINT_NAME, ColumnType.POINT_VALUE,
                ColumnType.POINT_TIME_STAMP, ColumnType.U_OF_M, ColumnType.STATE));
    }

    public static ColumnType getByTypeId(Integer typeId) {
        return lookupByTypeId.get(typeId);
    }
    
    public static ColumnType getByName(String name) {
        return lookupByName.get(name);
    }

    private int typeId;
    private String name;
    // width is needed for a backwards compatibility
    // for columns that can't be created via web TDC the width is 0.
    private int defaultWidth;

    ColumnType(int typeId, String name, int defaultWidth) {
        this.typeId = typeId;
        this.name = name;
        this.defaultWidth = defaultWidth;
    }

    public String getColumnName() {
        return name;
    }

    public int getTypeId() {
        return typeId;
    }
    
    public int getDefaultWidth() {
        return defaultWidth;
    }
    
    public static List<ColumnType> getDefaultColumns() {
        return defaultColumns;
    }
}
