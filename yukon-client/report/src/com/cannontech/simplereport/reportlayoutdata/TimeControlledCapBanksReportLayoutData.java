package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class TimeControlledCapBanksReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public TimeControlledCapBanksReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Area", "area", 80),
            new ColumnLayoutData("Substation", "substation", 80),
            new ColumnLayoutData("Substation Bus", "substationbus", 80),
            new ColumnLayoutData("Feeder", "feeder", 80),
            new ColumnLayoutData("CapBank", "capbank", 80),
            new ColumnLayoutData("Time of Close", "timeOfClose", 80),
            new ColumnLayoutData("Time of Open", "timeOfOpen", 80)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}