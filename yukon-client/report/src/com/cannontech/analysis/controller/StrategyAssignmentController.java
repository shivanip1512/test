package com.cannontech.analysis.controller;


import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.StrategyAssignmentReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.StrategyAssignmentModel;
import com.cannontech.spring.YukonSpringHook;

public class StrategyAssignmentController extends CapControlReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.STRATEGY};
    
    public StrategyAssignmentController() {
        super();
        model = (StrategyAssignmentModel) YukonSpringHook.getBean("strategyAssignmentModel");
        report = new StrategyAssignmentReport(model);
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

    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
    }

    public String getHTMLOptionsTable() {
        return "";
    }

}
