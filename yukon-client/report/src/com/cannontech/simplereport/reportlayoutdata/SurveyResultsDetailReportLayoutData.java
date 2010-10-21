package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class SurveyResultsDetailReportLayoutData implements ReportLayoutData {
    private ColumnLayoutData[] bodyColumns;

    public SurveyResultsDetailReportLayoutData() {
        bodyColumns = new ColumnLayoutData[] {
                new ColumnLayoutData("Account #", "accountNumber", 100),
                new ColumnLayoutData("Serial #", "serialNumber", 100),
                new ColumnLayoutData("Alt. Tracking #", "altTrackingNumber", 100),
                new ColumnLayoutData("Reason", "reason", 100),
                new ColumnLayoutData("Scheduled Date", "scheduledDate", 100),
                new ColumnLayoutData("Start Date", "startDate", 100, "DATEHM"),
                new ColumnLayoutData("End Date", "endDate", 100, "DATEHM"),
                new ColumnLayoutData("Load Program", "loadProgram", 100),
                new ColumnLayoutData("Gear", "gear", 100), };
    }

    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}
