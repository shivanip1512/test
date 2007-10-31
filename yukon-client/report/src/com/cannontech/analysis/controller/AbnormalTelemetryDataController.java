package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.AbnormalTelemetryDataReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.AbnormalTelemetryDataModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class AbnormalTelemetryDataController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public AbnormalTelemetryDataController() {
        super();
        model = new AbnormalTelemetryDataModel();
        report = new AbnormalTelemetryDataReport(model);
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
