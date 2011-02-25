package com.cannontech.analysis.controller;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapBankOperationsPerformanceReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapBankOperationsPerformanceModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

public class CapBankOperationsPerformanceController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    private TimeZone timeZone = TimeZone.getDefault();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String ATT_QUERY_TYPE = "queryType";
    protected static final String ATT_QUERY_PERCENT = "queryPercent";
    private String[] queryTypes = {"Success", "Failed", "Questionable", "Failed-Questionable"};
    
    public CapBankOperationsPerformanceController() {
        super();
        model = new CapBankOperationsPerformanceModel();
        report = new CapBankOperationsPerformanceReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td valign='top' class='TitleHeader'>Query Options</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        for( int i = 0; i < queryTypes.length; i++)
        {
            html += "        <tr>" + LINE_SEPARATOR;
            html += "          <td><input type='radio' name='" + ATT_QUERY_TYPE +"' value='" + queryTypes[i] + "' " +(i==0? "checked" : "") +" >" + queryTypes[i] + LINE_SEPARATOR;
            html += "          </td>" + LINE_SEPARATOR;
            html += "        </tr>" + LINE_SEPARATOR;
        }
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td>Percentage: <input type='text' name='" + ATT_QUERY_PERCENT +"' value='100' size='3'>";
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
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
        CapBankOperationsPerformanceModel performanceModel = (CapBankOperationsPerformanceModel) model;
        super.setRequestParameters(request);
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null) {
            ((CapBankOperationsPerformanceModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        } else {
            ((CapBankOperationsPerformanceModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null) {
            ((CapBankOperationsPerformanceModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapBankOperationsPerformanceModel)model).setStopDate(null);
        }
        
        String queryType = request.getParameter(ATT_QUERY_TYPE);
        performanceModel.setQueryType(queryType);
        String queryPercent = request.getParameter(ATT_QUERY_PERCENT);
        performanceModel.setQueryPercent(queryPercent);
    }
    
}
