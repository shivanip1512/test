package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class CapControlDisasbledDevicesReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public CapControlDisasbledDevicesReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device Name", "deviceName", 200)
        };
        
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
