package com.cannontech.analysis.controller;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlScheduleDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlScheduleDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

public class CapControlScheduleDetailController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    private TimeZone timeZone = TimeZone.getDefault();
    protected static final String ATT_ORDERBY = "orderBy";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String[] orderByTypes = {"Schedule", "Substation Bus", "Feeder", "Outgoing Command", "Last Run Time", "Next Run Time", "Interval"};
    
    public CapControlScheduleDetailController() {
        super();
        model = new CapControlScheduleDetailModel();
        report = new CapControlScheduleDetailReport(model);
    }

    public String getHTMLOptionsTable() {
String html = "";
        
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td valign='top' class='TitleHeader'>Sort By</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        for( int i = 0; i < orderByTypes.length; i++)
        {
            html += "        <tr>" + LINE_SEPARATOR;
            html += "          <td><input type='radio' name='" + ATT_ORDERBY +"' value='" + orderByTypes[i] + "' " +(i==0? "checked" : "") +" >" + orderByTypes[i] + LINE_SEPARATOR;
            html += "          </td>" + LINE_SEPARATOR;
            html += "        </tr>" + LINE_SEPARATOR;
        }
        html += "      </table>" + LINE_SEPARATOR;
        html += "    </td>" + LINE_SEPARATOR;

        html += "  </tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
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
        CapControlScheduleDetailModel performanceModel = (CapControlScheduleDetailModel) model;
        super.setRequestParameters(request);
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null)
            ((CapControlScheduleDetailModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        else
            ((CapControlScheduleDetailModel)model).setStartDate(null);
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null)
            ((CapControlScheduleDetailModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        else
            ((CapControlScheduleDetailModel)model).setStopDate(null);
        
        String orderBy = request.getParameter(ATT_ORDERBY);
        performanceModel.setOrderBy(orderBy);
    }
}
