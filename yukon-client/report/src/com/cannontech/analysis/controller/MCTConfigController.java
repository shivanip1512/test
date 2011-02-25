package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.MCTConfigReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MCTConfigModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;

public class MCTConfigController extends ReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {};
    
    public MCTConfigController() {
        super();
        model = YukonSpringHook.getBean("mctConfigModel", MCTConfigModel.class);
        report = new MCTConfigReport(model);
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