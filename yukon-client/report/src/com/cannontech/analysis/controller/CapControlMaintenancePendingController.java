package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapControlMaintenancePendingReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlMaintenancePendingModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CapControlMaintenancePendingController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public CapControlMaintenancePendingController() {
        super();
        model = new CapControlMaintenancePendingModel();
        report = new CapControlMaintenancePendingReport(model);
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