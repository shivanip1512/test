package com.cannontech.analysis.controller;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class ReportControllerAdapter implements ReportController {

    private final ReportModelBase reportModelbase;

    public ReportControllerAdapter(ReportModelBase reportModelbase) {
        super();
        this.reportModelbase = reportModelbase;
    }

    public String getHTMLBaseOptionsTable() {
        return reportModelbase.getHTMLBaseOptionsTable();
    }

    public String getHTMLOptionsTable() {
        return reportModelbase.getHTMLOptionsTable();
    }

    public YukonReportBase getReport() {
        return ReportFuncs.createYukonReport(reportModelbase);
    }
    
}
