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
    public String getColumnId() {
        
        String columnId = this.columnName.replaceAll("/", "");
        // Strip out '\'
        columnId = columnId.replaceAll("\\\\", "");
        columnId = columnId.replaceAll("&", "");
        columnId = columnId.replaceAll("<", "");
        columnId = columnId.replaceAll(">", "");
        columnId = columnId.replaceAll("\\s*", "");
        
        // Change the first letter to lower case
        String firstLetter  = columnId.substring(0, 1).toLowerCase();
        String returnString = firstLetter + columnId.substring(1);

        return returnString;
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
