package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class PowerFactorRPHReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public PowerFactorRPHReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Date/Time", "pointDataTimeStamp", 150, "MM/dd/yyyy HH:mm z"),
            new ColumnLayoutData("Power Factor", "powerFactor", 200, "SHORT"),
            new ColumnLayoutData("Estimated Power Factor", "estimatedPowerFactor", 200, "SHORT"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
