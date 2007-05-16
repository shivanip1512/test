package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapControlStateComparisonReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlStateComparisonModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CapControlStateComparisonController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public CapControlStateComparisonController() {
        super();
        model = new CapControlStateComparisonModel();
        report = new CapControlStateComparisonReport(model);
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
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

}
