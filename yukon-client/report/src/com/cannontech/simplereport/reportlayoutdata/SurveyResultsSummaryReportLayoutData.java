package com.cannontech.simplereport.reportlayoutdata;

import com.cannontech.analysis.report.ColumnLayoutData;

public class SurveyResultsSummaryReportLayoutData implements ReportLayoutData {
    private ColumnLayoutData[] bodyColumns;

    public SurveyResultsSummaryReportLayoutData() {
        bodyColumns = new ColumnLayoutData[] {
                new ColumnLayoutData("Control Begin", "controlBegin", 100, "DATEHM"),
                new ColumnLayoutData("Control End", "controlEnd", 100, "DATEHM"),
                new ColumnLayoutData("Reason", "reason", 300),
                new ColumnLayoutData("# Devices Overridden",
                                     "numDevicesOverridden", 75),
                new ColumnLayoutData("Load Program", "loadProgram", 100),
                new ColumnLayoutData("Gear", "gear", 100), };
    }

    public ColumnLayoutData[] getBodyColumns() {
        return bodyColumns;
    }
}
