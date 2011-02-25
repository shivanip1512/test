package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.YukonReportBase;


public interface ReportController {
    
    public String getHTMLOptionsTable();
    public YukonReportBase getReport();
    
    public LinkedHashMap<ReportFilter, List<? extends Object>> getFilterObjectsMap(int userId);
    public boolean reportHasFilter(int userId);
    public void setRequestParameters(HttpServletRequest request);
    public boolean useStartStopTimes();
    public boolean supportsPdf();
    
}
