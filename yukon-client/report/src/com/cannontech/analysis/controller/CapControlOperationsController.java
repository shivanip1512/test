package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.CapControlOperationsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.database.model.ModelFactory;

public class CapControlOperationsController extends CapControlReportControllerBase {
    
    private int[] filterModelTypes = new int[]{
            ModelFactory.CAPCONTROLSTRATEGY,
            ModelFactory.CAPBANK,
            ModelFactory.CAPCONTROLFEEDER};
    
    public CapControlOperationsController() {
        super();
        model = new CapControlOperationsModel();
        report = new CapControlOperationsReport(model);
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
