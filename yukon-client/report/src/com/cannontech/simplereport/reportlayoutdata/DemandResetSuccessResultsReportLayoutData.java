package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class DemandResetSuccessResultsReportLayoutData implements ReportLayoutData {

    private ColumnLayoutData[] bodyColumns;
    
    public DemandResetSuccessResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
                
            new ColumnLayoutData("Device Name", "deviceName", 400),
            new ColumnLayoutData("Current State", "state", 165),
            new ColumnLayoutData("Timestamp", "timestamp", 165),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
