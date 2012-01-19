package com.cannontech.common.bulk.service;

/**
 * Enum representation of all default (i.e. non-interface-specific)
 * column headers used in FDR translation import and export files.
 */
public enum FdrCsvHeader {
    ACTION(null, false),
    DEVICE_NAME(0, true),
    DEVICE_TYPE(1, true),
    POINT_NAME(2, true),
    DIRECTION(3, true);
    
    private static Integer colsToExport;
    
    private Integer columnIndex;
    private boolean export;
    
    private FdrCsvHeader(Integer columnIndex, boolean export) {
        this.columnIndex = columnIndex;
    }
    
    /**
     * Indicates the appropriate order of the columns for exported
     * files. Import files should not require a specific ordering.
     */
    public Integer getIndex() {
        return columnIndex;
    }
    
    /**
     * @return true if the column is used in export files, otherwise false.
     */
    public boolean isExported() {
        return export;
    }
    
    /**
     * Indicates the total number of default columns used in export files.
     * This value can be used to find the beginning of interface-specific
     * columns in a data row that includes default columns at their
     * expected indices.
     */
    public static int getNumberOfDefaultColumnsExported() {
        if(colsToExport == null) {
            for(FdrCsvHeader header : values()) {
                if(header.isExported()) colsToExport++;
            }
        }
        return colsToExport;
    }
}
