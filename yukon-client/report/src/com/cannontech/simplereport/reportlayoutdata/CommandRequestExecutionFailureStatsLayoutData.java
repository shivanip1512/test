package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class CommandRequestExecutionFailureStatsLayoutData implements ReportLayoutData {

	private ColumnLayoutData[] bodyColumns;
    
    public CommandRequestExecutionFailureStatsLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Status", "status", 500),
            new ColumnLayoutData("Count", "count", 100),
            new ColumnLayoutData("Failure Percentage", "percentage", 100),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}