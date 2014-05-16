package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class DisconnectAllResultsReportLayoutData implements ReportLayoutData {

    private ColumnLayoutData[] bodyColumns;
    
    public DisconnectAllResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
                
            new ColumnLayoutData("Device Name", "deviceName", 200),
            new ColumnLayoutData("Current State", "state", 100),
            new ColumnLayoutData("Timestamp", "timestamp", 100),
            new ColumnLayoutData("Error Description", "errorDescription", 280),
            new ColumnLayoutData("Error Code", "errorCode", 50),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
