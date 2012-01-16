package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class GroupActionFailureResultsReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public GroupActionFailureResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
                
            new ColumnLayoutData("Device Name", "deviceName", 150),
            new ColumnLayoutData("Error Description", "errorDescription", 400),
            new ColumnLayoutData("Error Code", "errorCode", 50),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
