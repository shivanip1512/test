package com.cannontech.common.events.model;

import com.google.common.base.CharMatcher;

public class ArgumentColumn {
    private final String columnName;
    private final int sqlType;
    
    public ArgumentColumn(String columnName, int sqlType) {
        this.columnName = columnName;
        this.sqlType = sqlType;
    }
    
    @Override
    public String toString() {
        return columnName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * Helper method to return the index from the columnName
     * String3 -> 3
     * Date12  -> 12
     */
    public Integer getColumnIndex() {
        return Integer.valueOf(CharMatcher.inRange('0', '9').retainFrom(getColumnName()));
    }
    
    public int getSqlType() {
        return sqlType;
    }
    
}