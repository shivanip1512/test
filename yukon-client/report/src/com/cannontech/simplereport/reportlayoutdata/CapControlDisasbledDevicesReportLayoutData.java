package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class CapControlDisasbledDevicesReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public CapControlDisasbledDevicesReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device Name", "deviceName", 100),
            new ColumnLayoutData("Device Type", "deviceType", 100),
            new ColumnLayoutData("Device Parent", "deviceParent", 150),
            new ColumnLayoutData("Date/Time", "dateTime", 100),
            new ColumnLayoutData("User", "user", 100),
            new ColumnLayoutData("Comment", "comment", 180),
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
