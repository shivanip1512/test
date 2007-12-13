package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class BulkImportResultsReportLayoutData implements ReportLayoutData{

    private ColumnLayoutData[] bodyColumns;
    
    public BulkImportResultsReportLayoutData() {
        
        this.bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Address", "address", 70),
            new ColumnLayoutData("Name", "name", 70),
            new ColumnLayoutData("Route Name", "routeName", 80),
            new ColumnLayoutData("Meter Number", "meterNumber", 70),
            new ColumnLayoutData("Collection Group", "collectionGroup", 90),
            new ColumnLayoutData("Alt Group", "altGroup", 70),
            new ColumnLayoutData("Template Name", "templateName", 100),
            new ColumnLayoutData("Billing Group", "billingGroup", 70),
            new ColumnLayoutData("Substation Name", "substationName", 90),
        };
    }
    
    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
    
}
