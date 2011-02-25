package com.cannontech.analysis.controller;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlRetriesReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlRetriesModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

public class CapControlRetriesController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    private TimeZone timeZone = TimeZone.getDefault();
    
    public CapControlRetriesController() {
        super();
        model = new CapControlRetriesModel();
        report = new CapControlRetriesReport(model);
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
    
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        
        if( param != null) {
            ((CapControlRetriesModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapControlRetriesModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        
        if( param != null) {
            ((CapControlRetriesModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapControlRetriesModel)model).setStopDate(null);
        }
    }

}

