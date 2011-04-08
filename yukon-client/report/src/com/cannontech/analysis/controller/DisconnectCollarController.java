package com.cannontech.analysis.controller;

import com.cannontech.analysis.report.DisconnectCollarReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DisconnectCollarModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;

public class DisconnectCollarController extends MeterReportControllerBase {
    
    public DisconnectCollarController() {
        super();
        model = (DisconnectCollarModel)YukonSpringHook.getBean("disconnectCollarModel");
        report = new DisconnectCollarReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        
    return html;
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<DisconnectCollarModel> getModel() {
        return report.getModel();
    }
}
