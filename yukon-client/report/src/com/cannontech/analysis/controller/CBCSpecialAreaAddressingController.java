package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CBCSpecialAreaAddressingReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CBCSpecialAreaAddressingModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CBCSpecialAreaAddressingController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public CBCSpecialAreaAddressingController() {
        super();
        model = new CBCSpecialAreaAddressingModel();
        report = new CBCSpecialAreaAddressingReport(model);
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase getModel() {
        return report.getModel();
    }
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    @Override
    public String getHTMLOptionsTable() {
        return "";
    }
    
}
