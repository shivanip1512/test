package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.report.ZigbeeControlledDeviceReport;
import com.cannontech.analysis.tablemodel.ZigbeeControlledDeviceModel;

public class ZigbeeControlledDeviceController extends ReportControllerBase {

    public ZigbeeControlledDeviceController() {
        super();
        model = new ZigbeeControlledDeviceModel();
        report = new ZigbeeControlledDeviceReport(model);
    }
    
    @Override
    public String getHTMLOptionsTable() {
        return "";
    }

    @Override
    public YukonReportBase getReport() {
        return report;
    }

    @Override
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        
    }
}
