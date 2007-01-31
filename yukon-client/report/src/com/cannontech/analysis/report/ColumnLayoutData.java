package com.cannontech.analysis.report;

import org.jfree.report.ElementAlignment;

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

    public final Integer modelIndex;
    public final Integer width;
    public final String format;
    public final ElementAlignment horizontalAlignment;

    public ColumnLayoutData(Integer modelIndex, Integer width) {
        this(modelIndex, width, null);
    }

    public ColumnLayoutData(Integer modelIndex, Integer width, String format) {
        this(modelIndex, width, format, null);
    }
    
    public ColumnLayoutData(Integer modelIndex, Integer width, String format, ElementAlignment horizontalAlignment) {
        this.modelIndex = modelIndex;
        this.width = width;
        this.format = format;
        this.horizontalAlignment = horizontalAlignment;
    }
    
}
