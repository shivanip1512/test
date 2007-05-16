package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CBCInventoryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CBCInventoryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CBCInventoryController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[]{
    		ReportFilter.CAPCONTROLSUBBUS,
    		ReportFilter.CAPBANK,
    		ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};

    public CBCInventoryController() {
        super();
        model = new CBCInventoryModel();
        report = new CBCInventoryReport(model);
    }

    public String getHTMLOptionsTable() {
        return "";
    }

    public YukonReportBase getReport() {
        return report;
    }

    public ReportModelBase getModel() {
        return report.getModel();
    }
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

}
