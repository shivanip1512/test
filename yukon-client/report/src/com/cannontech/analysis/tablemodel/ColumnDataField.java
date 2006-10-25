package com.cannontech.analysis.tablemodel;

import java.lang.reflect.Field;

import com.cannontech.clientutils.CTILogger;

/**
 * This class attempts to bridge the gap between a row model class and a list of columns.
 * The constructor takes a fieldName which should match a public field in the row model
 * class declared in the BareReportModelBase class. Reflection is then used to get
 * both the type of the field and its value.
 */
class ColumnDataField implements ColumnData {
    private String columnName;
    private String fieldName;
    public ColumnDataField(String columnName, String fieldName) {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }
    
    public Class<?> getColumnType(Class<?> clazz) {
        try {
            Field field = clazz.getField(fieldName);
            if (field.getType().isPrimitive()) {
                throw new IllegalArgumentException("Using primitive types in the model is not supported: " + clazz);
            }
            return field.getType();
        } catch (NoSuchFieldException e) {
            CTILogger.error(e);
        }
        return null;
    }
    
    public Object getColumnValue(Object o) {
        try {
            Field field = o.getClass().getField(fieldName);
            return field.get(o);
        } catch (NoSuchFieldException  e) {
            CTILogger.error(e);
        } catch (IllegalAccessException e) {
            CTILogger.error(e);
        }
        return null;
    }

    public String getColumnName() {
        return columnName;
    }
}