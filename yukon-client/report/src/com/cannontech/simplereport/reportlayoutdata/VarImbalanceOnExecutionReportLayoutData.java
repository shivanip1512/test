package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class VarImbalanceOnExecutionReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public VarImbalanceOnExecutionReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Area", "area", 80),
            new ColumnLayoutData("Substation", "substation", 80),
            new ColumnLayoutData("Substation Bus", "substationbus", 80),
            new ColumnLayoutData("Feeder", "feeder", 80),
            new ColumnLayoutData("CapBank", "capbank", 80),
            new ColumnLayoutData("CaBank State", "capbankstate", 80),
            new ColumnLayoutData("A Change", "achange", 80),
            new ColumnLayoutData("B Change", "bchange", 80),
            new ColumnLayoutData("C Change", "cchange", 80)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
