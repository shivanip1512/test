package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class ReportControllerAdapter extends ReportControllerBase  {

    private final ReportModelBase reportModelbase;

    public ReportControllerAdapter(ReportModelBase reportModelbase) {
        super();
        this.reportModelbase = reportModelbase;
    }

    public String getHTMLOptionsTable() {
        return reportModelbase.getHTMLOptionsTable();
    }

    public YukonReportBase getReport() {
        return ReportFuncs.createYukonReport(reportModelbase);
    }
    
    @Override
    public ReportFilter[] getFilterModelTypes() {
        return reportModelbase.getFilterModelTypes();
    }
    
    @Override
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        reportModelbase.setParameters(request);
    }
    
    public boolean useStartStopTimes() {
        return reportModelbase.useStartStopTimes();
    }
    
}
