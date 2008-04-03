package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class KVarPhaseWattRPHReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public KVarPhaseWattRPHReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Date/Time", "pointDataTimeStamp", 150, "MM/dd/yyyy HH:mm z"),
            new ColumnLayoutData("Phase A", "phaseA", 120, "SHORT"),
            new ColumnLayoutData("Phase B", "phaseB", 120, "SHORT"),
            new ColumnLayoutData("Phase C", "phaseC", 120, "SHORT"),
            new ColumnLayoutData("Estimated Var Load", "estimatedVarLoad", 120, "SHORT"),
            new ColumnLayoutData("Current Watt Load", "currentWattLoad", 120, "SHORT"),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
