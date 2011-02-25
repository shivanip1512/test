
package com.cannontech.analysis.controller;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlAssetUnavailabilityReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlAssetUnavailabilityModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

public class CapControlAssetUnavailabilityController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            };
    
    private TimeZone timeZone = TimeZone.getDefault();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public CapControlAssetUnavailabilityController() {
        super();
        model = new CapControlAssetUnavailabilityModel();
        report = new CapControlAssetUnavailabilityReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        return html;
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
    
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);

        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null) {
            ((CapControlAssetUnavailabilityModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        } else {
            ((CapControlAssetUnavailabilityModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null) {
            ((CapControlAssetUnavailabilityModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapControlAssetUnavailabilityModel)model).setStopDate(null);
        }
    }
}
