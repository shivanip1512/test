package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.LMControlDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LMControlDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class LMControlDetailController extends ReportControllerBase {
    
    public LMControlDetailController() {
        super();
        model = new LMControlDetailModel();
        report = new LMControlDetailReport(model);
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
