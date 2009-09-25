package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class PhaseDetectReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public PhaseDetectReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("MCT Name", "mctName", 150),
            new ColumnLayoutData("Route Name", "routeName", 150),
            new ColumnLayoutData("Phase Detected", "phaseDetected", 100),
            new ColumnLayoutData("Phase A Reading", "phaseAReading", 100),
            new ColumnLayoutData("Phase B Reading", "phaseBReading", 100),
            new ColumnLayoutData("Phase C Reading", "phaseCReading", 100),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}