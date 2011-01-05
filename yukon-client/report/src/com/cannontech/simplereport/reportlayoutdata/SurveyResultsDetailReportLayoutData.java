package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class SurveyResultsDetailReportLayoutData implements ReportLayoutData {
    private final static ColumnLayoutData[] bodyColumns =
        new ColumnLayoutData[] {
            new ColumnLayoutData("Account #", "accountNumber", 70),
            new ColumnLayoutData("Serial #", "serialNumber", 70),
            new ColumnLayoutData("Alt. Tracking #", "altTrackingNumber", 70),
            new ColumnLayoutData("Reason", "reason", 100),
            new ColumnLayoutData("Scheduled Date", "scheduledDate", 85, "DATEHM"),
            new ColumnLayoutData("Start Date", "startDate", 85, "DATEHM"),
            new ColumnLayoutData("End Date", "endDate", 85, "DATEHM"),
            new ColumnLayoutData("Load Program", "loadProgram", 100),
            new ColumnLayoutData("Gear", "gear", 100)
    };

    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}
