package com.cannontech.simplereport;

public class ColumnInfo {

    private String label;
    private Integer width;
    private String align;
    private Integer columnWidthPercentage;
    
    public ColumnInfo() {
    }

    public ColumnInfo(String columnName, Integer columnWidth, String columnAlignment) {
        
        this.label = columnName;
        this.width = columnWidth;
        this.align = columnAlignment;
    }
    
    public void calculateColumnWidthPercentage(Integer totalWidth) {
        setColumnWidthPercentage(Math.round((width / (float)totalWidth) * 100));
    }
    
    
    public String getLabel() {
        return label;
    }
    public void setLabel(String columnName) {
        this.label = columnName;
    }
    public String getName() {
        
        String columnId = this.label.replaceAll("/", "");
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
    public int getWidth() {
        return width;
    }
    public void setWidth(int columnWidth) {
        this.width = columnWidth;
    }
    public String getAlign() {
        return align;
    }
    public void setAlign(String columnAlignment) {
        this.align = columnAlignment;
    }
    public Integer getColumnWidthPercentage() {
        return columnWidthPercentage;
    }
    public void setColumnWidthPercentage(Integer columnWidthPercentage) {
        this.columnWidthPercentage = columnWidthPercentage;
    }
}
