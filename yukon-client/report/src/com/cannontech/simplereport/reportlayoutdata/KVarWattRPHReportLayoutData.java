package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class KVarWattRPHReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public KVarWattRPHReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Date/Time", "pointDataTimeStamp", 150, "MM/dd/yyyy HH:mm z"),
            new ColumnLayoutData("Current Var Load", "currentVarLoad", 150, "SHORT"),
            new ColumnLayoutData("Estimated Var Load", "estimatedVarLoad", 150, "SHORT"),
            new ColumnLayoutData("Current Watt Load", "currentWattLoad", 150, "SHORT"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
