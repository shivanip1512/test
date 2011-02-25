package com.cannontech.analysis.controller;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlMaintenancePendingReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlMaintenancePendingModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class CapControlMaintenancePendingController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
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