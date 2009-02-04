package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.report.SimpleYukonReportBase;

public class StrategyAssignmentReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public StrategyAssignmentReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device", "paoName", 200),
            new ColumnLayoutData("Type", "type", 100),
            new ColumnLayoutData("Control Method", "controlMethod", 100),
            new ColumnLayoutData("Season", "seasonName", 100),
            new ColumnLayoutData("Start Date", "seasonStartDate", 100, SimpleYukonReportBase.columnDateFormat),
            new ColumnLayoutData("End Date", "seasonEndDate", 100, SimpleYukonReportBase.columnDateFormat)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
