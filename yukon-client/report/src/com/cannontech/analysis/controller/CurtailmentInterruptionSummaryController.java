package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CurtailmentInterruptionSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CurtailmentInterruptionSummaryModel;

public class CurtailmentInterruptionSummaryController extends ReportControllerBase {

    public CurtailmentInterruptionSummaryController() {
        super();
        model = new CurtailmentInterruptionSummaryModel();
        report = new CurtailmentInterruptionSummaryReport(model);
    }

    public String getHTMLBaseOptionsTable() {
        return "";
    }

    public String getHTMLOptionsTable() {
        return "";
    }

    public YukonReportBase getReport() {
        return report;
    }

}
