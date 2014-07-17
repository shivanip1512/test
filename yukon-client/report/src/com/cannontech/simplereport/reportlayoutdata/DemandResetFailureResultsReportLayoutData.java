package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class DemandResetFailureResultsReportLayoutData implements ReportLayoutData{

    private final ColumnLayoutData[] bodyColumns;
    
    public DemandResetFailureResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
                
            new ColumnLayoutData("Device Name", "deviceName", 150),
            new ColumnLayoutData("Current State", "state", 100),
            new ColumnLayoutData("Error Description", "errorDescription", 430),
            new ColumnLayoutData("Error Code", "errorCode", 50),
        };
    }
    
    @Override
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
