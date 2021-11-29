package com.cannontech.analysis.report;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

/**
 * This is used to specify the layout data for a column. The model index
 * must be indicated because instances of these class could occur in any 
 * order to indicate the columns appear in the report in a different order
 * than they appear in the model. The width is taken in pixels and is used 
 * internally not only to determine the width by the x-offset as well. The
 * format string is used for Number and Date type columns and is either used by
 * DecimalFormat or SimpleDateFormat.
 */
public class ColumnLayoutData {

    private final String columnName;
    private final String fieldName;
    private final Integer width;
    private String format = null;
    private ElementAlignment horizontalAlignment = null;

    public ColumnLayoutData(String columnName, String fieldName, Integer width, String format, ElementAlignment horizontalAlignment) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.width = width;
        this.format = format;
        this.horizontalAlignment = horizontalAlignment;
    }
    
    public ColumnLayoutData(String columnName, String fieldName, Integer width, String format) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.width = width;
        this.format = format;
    }

    public ColumnLayoutData(String columnName, String fieldName, Integer width) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.width = width;
    }

    public ColumnLayoutData(String columnName, Integer width) {
        this.columnName = columnName;
        this.fieldName= columnName;
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public ColumnLayoutData setFormat(String format) {
        this.format = format;
        return this;
    }

    public ElementAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public ColumnLayoutData setHorizontalAlignment(ElementAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Integer getWidth() {
        return width;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((columnName == null) ? 0 : columnName.hashCode());
        result = PRIME * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ColumnLayoutData other = (ColumnLayoutData) obj;
        if (columnName == null) {
            if (other.columnName != null)
                return false;
        } else if (!columnName.equals(other.columnName))
            return false;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        return true;
    }
    

    
}
