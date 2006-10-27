package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CurtailmentEventSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CurtailmentEventSummaryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class CurtailmentEventSummaryController implements ReportController {

    private CurtailmentEventSummaryModel model;
    private CurtailmentEventSummaryReport report;

    public CurtailmentEventSummaryController() {
        super();
        model = new CurtailmentEventSummaryModel();
        report = new CurtailmentEventSummaryReport(model);
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

    public ReportModelBase getModel() {
        return report.getModel();
    }

}
