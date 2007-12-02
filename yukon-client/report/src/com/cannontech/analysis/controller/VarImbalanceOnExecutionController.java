package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.VarImbalanceOnExecutionReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.VarImbalanceOnExecutionModel;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.spring.YukonSpringHook;

public class VarImbalanceOnExecutionController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    public VarImbalanceOnExecutionController() {
        super();
        model = (VarImbalanceOnExecutionModel)YukonSpringHook.getBean("varImbalanceOnExecutionModel");
        report = new VarImbalanceOnExecutionReport(model);
    }

    public String getHTMLOptionsTable() {
        return "";
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
    
}