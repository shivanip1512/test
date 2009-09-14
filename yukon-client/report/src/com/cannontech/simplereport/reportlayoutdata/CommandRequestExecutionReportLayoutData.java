package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class CommandRequestExecutionReportLayoutData implements ReportLayoutData {

	private ColumnLayoutData[] bodyColumns;
    
    public CommandRequestExecutionReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Command", "command", 200),
            new ColumnLayoutData("Status", "status", 200),
            new ColumnLayoutData("Device Name", "deviceName", 180),
            new ColumnLayoutData("Route Name", "routeName", 150),
            new ColumnLayoutData("Completed", "completeTime", 150, "MM/dd/yyyy HH:mm z"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}
