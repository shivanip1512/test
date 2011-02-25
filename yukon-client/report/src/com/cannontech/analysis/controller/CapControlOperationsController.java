package com.cannontech.analysis.controller;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlOperationsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

public class CapControlOperationsController extends CapControlReportControllerBase {
    
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
    private String[] orderByTypes = {
            "CBC", 
            "Operation Time", 
            "Operation", 
            "Conf Time", 
            "Conf Status", 
            "Status Quality", 
            "Feeder", 
            "Substation Bus", 
            "Area",
            "Bank Size",
            "IP Address",
            "Serial Num", 
            "Slave Address"
            };
    protected static final String ATT_STATUS_QUALITIES = "statusQualities";
    
    public CapControlOperationsController() {
        super();
        model = new CapControlOperationsModel();
        report = new CapControlOperationsReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        html += "<table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell' checked>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td style='vertical-align:top;'>" + LINE_SEPARATOR;
        html += "<table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell' checked>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td class='TitleHeader'>&nbsp;Cap Bank Status Quality</td>" +LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Normal' checked>Normal"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Partial' checked>Partial"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Significant' checked>Significant"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Abnormal Quality' checked>Abnormal Quality"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Fail' checked>Fail"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='Comm Fail' checked>Comm Fail"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='No Control' checked>No Control"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_STATUS_QUALITIES+"' value='UnSolicited' checked>UnSolicited"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        html += "</td>";
        html += "<td>";
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td valign='top' class='TitleHeader'>Sort By</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        for( int i = 0; i < orderByTypes.length; i++) {
            html += "        <tr>" + LINE_SEPARATOR;
            html += "          <td><input type='radio' name='" + ATT_ORDERBY +"' value='" + orderByTypes[i] + "' " +( i==1 ? "checked" : "") +" >" + orderByTypes[i] + LINE_SEPARATOR;
            html += "          </td>" + LINE_SEPARATOR;
            html += "        </tr>" + LINE_SEPARATOR;
        }
        html += "      </table>" + LINE_SEPARATOR;
        html += "    </td>" + LINE_SEPARATOR;

        html += "  </tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        html += "</td>";
        html += "</tr>";
        html += "</table>";
        return html;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlOperationsModel operationsModel = (CapControlOperationsModel) model;
        super.setRequestParameters(request);
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null) {
            ((CapControlOperationsModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        } else {
            ((CapControlOperationsModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null) {
            ((CapControlOperationsModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapControlOperationsModel)model).setStopDate(null);
        }
        
        String[] statusQualities = request.getParameterValues(ATT_STATUS_QUALITIES);
        operationsModel.setStatusQualities(statusQualities);
        
        String orderBy = request.getParameter(ATT_ORDERBY);
        operationsModel.setOrderBy(orderBy);
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
