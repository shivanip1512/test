package com.cannontech.analysis.controller;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.TimeControlledCapBanksReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.TimeControlledCapBanksModel;
import com.cannontech.spring.YukonSpringHook;

public class TimeControlledCapBanksController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS
            };
    
    public TimeControlledCapBanksController() {
        super();
        model = (TimeControlledCapBanksModel)YukonSpringHook.getBean("timeControlledCapBanksModel");
        report = new TimeControlledCapBanksReport(model);
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