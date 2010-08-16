package com.cannontech.common.events.model;

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

    public int getSqlType() {
        return sqlType;
    }
    
}