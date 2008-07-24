package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class DailyUsageReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public DailyUsageReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Date", "date", 300, "MM/dd/yyyy"),
            new ColumnLayoutData("Value", "value", 250, null),
            new ColumnLayoutData("Unit", "units", 150, null)
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
