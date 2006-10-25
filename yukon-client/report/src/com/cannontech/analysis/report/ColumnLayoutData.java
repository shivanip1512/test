package com.cannontech.analysis.report;

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

    public ColumnLayoutData(Integer modelIndex, Integer width) {
        super();
        this.modelIndex = modelIndex;
        this.width = width;
        this.format = null;
    }

    public ColumnLayoutData(Integer modelIndex, Integer width, String format) {
        super();
        this.modelIndex = modelIndex;
        this.width = width;
        this.format = format;
    }
    
}
