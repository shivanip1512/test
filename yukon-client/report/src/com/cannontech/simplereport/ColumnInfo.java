package com.cannontech.simplereport;

public class ColumnInfo {

    private String columnName;
    private Integer columnWidth;
    private String columnFormat;
    private String columnAlignment;
    private Integer columnWidthPercentage;
    
    public void calculateColumnWidthPercentage(Integer totalWidth) {
        setColumnWidthPercentage(Math.round((columnWidth / (float)totalWidth) * 100));
    }
    
    
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public int getColumnWidth() {
        return columnWidth;
    }
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }
    public String getColumnAlignment() {
        return columnAlignment;
    }
    public void setColumnAlignment(String columnAlignment) {
        this.columnAlignment = columnAlignment;
    }
    public Integer getColumnWidthPercentage() {
        return columnWidthPercentage;
    }
    public void setColumnWidthPercentage(Integer columnWidthPercentage) {
        this.columnWidthPercentage = columnWidthPercentage;
    }
    public String getColumnFormat() {
        return columnFormat;
    }
    public void setColumnFormat(String columnFormat) {
        this.columnFormat = columnFormat;
    }
    
    
}
