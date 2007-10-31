package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapBankMaxOpsAlarmsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapBankMaxOpsAlarmsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CapBankMaxOpsAlarmsController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public CapBankMaxOpsAlarmsController() {
        super();
        model = new CapBankMaxOpsAlarmsModel();
        report = new CapBankMaxOpsAlarmsReport(model);
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

