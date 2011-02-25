package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class ReportControllerAdapter extends ReportControllerBase  {

    private final ReportModelBase<?> reportModelbase;

    public ReportControllerAdapter(ReportModelBase<?> reportModelbase) {
        super();
        this.reportModelbase = reportModelbase;
    }
    
    @Override
    public LinkedHashMap<ReportFilter, List<? extends Object>> getFilterObjectsMap(int userId) {
        if (reportModelbase instanceof FilterObjectsMapSource) {
            FilterObjectsMapSource filterSource = (FilterObjectsMapSource) reportModelbase;
            return filterSource.getFilterObjectsMap(userId);
        }
        return super.getFilterObjectsMap(userId);
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
    
    public boolean supportsPdf() {
        return getReport().supportsPdf();
    }
}