package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class DisconnectCollarReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public DisconnectCollarReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device Name", "deviceName", 200),
            new ColumnLayoutData("Enabled", "enabled", 80),
            new ColumnLayoutData("Type", "deviceType", 80),
            new ColumnLayoutData("Meter #", "meterNumber", 80),
            new ColumnLayoutData("Address", "physicalAddress", 80),
            new ColumnLayoutData("Disconnect Address", "disconnectAddress", 120),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
