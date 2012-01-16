package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class GroupActionSuccessResultsReportLayoutData implements ReportLayoutData {

    private ColumnLayoutData[] bodyColumns;
    
    public GroupActionSuccessResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
                
            new ColumnLayoutData("Device Name", "deviceName", 120),
            new ColumnLayoutData("Point Name", "pointName", 100),
            new ColumnLayoutData("Point Type", "pointType", 100),
            new ColumnLayoutData("Point Value", "pointValue", 100, "SHORT"),
            new ColumnLayoutData("Point Timestamp", "pointTimeStamp", 120, "BOTH"),
            new ColumnLayoutData("Last Result", "lastResult", 300),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
