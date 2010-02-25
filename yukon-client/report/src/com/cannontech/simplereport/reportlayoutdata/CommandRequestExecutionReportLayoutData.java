package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class CommandRequestExecutionReportLayoutData implements ReportLayoutData {

	private ColumnLayoutData[] bodyColumns;
    
    public CommandRequestExecutionReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Command", "command", 120),
            new ColumnLayoutData("Status", "status", 250),
            new ColumnLayoutData("Device Name", "deviceName", 130),
            new ColumnLayoutData("Route Name", "routeName", 130),
            new ColumnLayoutData("Completed", "completeTime", 100, "BOTH"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}
