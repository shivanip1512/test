package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapControlOperationsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CapControlOperationsController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
    		ReportFilter.CAPCONTROLSUBBUS,
    		ReportFilter.CAPBANK,
    		ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public CapControlOperationsController() {
        super();
        model = new CapControlOperationsModel();
        report = new CapControlOperationsReport(model);
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
