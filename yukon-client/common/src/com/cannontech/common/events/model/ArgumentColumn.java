package com.cannontech.common.events.model;

public class ArgumentColumn {
    public ArgumentColumn(String columnName, int columnNumber, int sqlType) {
        this.columnName = columnName;
        this.columnNumber = columnNumber;
        this.sqlType = sqlType;
    }
    public final String columnName;
    public final int columnNumber;
    public final int sqlType;
    
    @Override
    public String toString() {
        return columnName;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    
}