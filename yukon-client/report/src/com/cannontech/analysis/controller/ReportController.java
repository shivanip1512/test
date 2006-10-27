package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.YukonReportBase;


public interface ReportController {
    public String getHTMLBaseOptionsTable();
    public String getHTMLOptionsTable();
    public YukonReportBase getReport();
    
}
