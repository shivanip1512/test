package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapControlStateComparisonReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlStateComparisonModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.database.model.ModelFactory;

public class CapControlStateComparisonController extends CapControlReportControllerBase {

    private int[] filterModelTypes = new int[]{
            ModelFactory.CAPCONTROLSTRATEGY,
            ModelFactory.CAPBANK,
            ModelFactory.CAPCONTROLFEEDER};
    
    public CapControlStateComparisonController() {
        super();
        model = new CapControlStateComparisonModel();
        report = new CapControlStateComparisonReport(model);
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
    
    public int[] getFilterModelTypes() {
        return filterModelTypes;
    }

}
