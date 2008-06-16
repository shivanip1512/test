package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;


public interface ReportController {
    
    public String getHTMLOptionsTable();
    public YukonReportBase getReport();
    
    public LinkedHashMap<ReportFilter, List<? extends Object>> getFilterObjectsMap();
    public void setRequestParameters(HttpServletRequest request);
    
}
