package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class RawPointHistoryReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public RawPointHistoryReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Date/Time", "pointDataTimeStamp", 150, "MM/dd/yyyy HH:mm z"),
            new ColumnLayoutData("Value", "valueHolder", 500, "SHORT"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
