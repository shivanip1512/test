package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class GroupDevicesReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public GroupDevicesReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Name", "name", 150),
            new ColumnLayoutData("Meter Number", "meterNumber", 100),
            new ColumnLayoutData("Device Type", "deviceType", 100),
            new ColumnLayoutData("Address", "address", 100),
            new ColumnLayoutData("Route", "route", 100),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
