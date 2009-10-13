package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.report.SimpleYukonReportBase;

public class LCR3102DataReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public LCR3102DataReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Serial Number", "serialNumber", 100),
            new ColumnLayoutData("Route", "route", 140),
            new ColumnLayoutData("Run Time Load", "runTimeLoad", 100),
            new ColumnLayoutData("Relay Shed Time", "relayShedTime", 100),
            new ColumnLayoutData("Date/Time", "timestamp", 120, SimpleYukonReportBase.columnDateFormat),
            new ColumnLayoutData("Relay #", "relay", 100)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}