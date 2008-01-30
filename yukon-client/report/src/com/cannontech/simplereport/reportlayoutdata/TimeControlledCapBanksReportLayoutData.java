package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class TimeControlledCapBanksReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public TimeControlledCapBanksReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Area", "area", 100),
            new ColumnLayoutData("Substation", "substation", 100),
            new ColumnLayoutData("Substation Bus", "substationbus", 100),
            new ColumnLayoutData("Strategy", "strategyName", 100),
            new ColumnLayoutData("Control Method", "controlMethod", 100),
            new ColumnLayoutData("Start Time", "startTimeSeconds", 100),
            new ColumnLayoutData("Percent of Banks to Close", "percentOfBanksToClose", 130)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}