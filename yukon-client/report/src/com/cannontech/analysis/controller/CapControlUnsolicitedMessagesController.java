package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlUnsolicitedMessagesReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlUnsolicitedMessagesModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class CapControlUnsolicitedMessagesController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    protected static final String ATT_ORDERBY = "orderBy";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String[] orderByTypes = {"Area", "Substation Bus", "Feeder", "Cap Bank", "CBC", "Date/Time", "Reason", "State", "Ip Address"};
    
    public CapControlUnsolicitedMessagesController() {
        super();
        model = new CapControlUnsolicitedMessagesModel();
        report = new CapControlUnsolicitedMessagesReport(model);
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
    
    public String getHTMLOptionsTable() {
        String html = "";
        
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td valign='top' class='TitleHeader'>Sort By</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        for( int i = 0; i < orderByTypes.length; i++) {
            html += "        <tr>" + LINE_SEPARATOR;
            html += "          <td><input type='radio' name='" + ATT_ORDERBY +"' value='" + orderByTypes[i] + "' " +( i==5 ? "checked" : "") +" >" + orderByTypes[i] + LINE_SEPARATOR;
            html += "          </td>" + LINE_SEPARATOR;
            html += "        </tr>" + LINE_SEPARATOR;
        }
        html += "      </table>" + LINE_SEPARATOR;
        html += "    </td>" + LINE_SEPARATOR;

        html += "  </tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        return html;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlUnsolicitedMessagesModel theModel = (CapControlUnsolicitedMessagesModel) model;
        super.setRequestParameters(request);

        String orderBy = request.getParameter(ATT_ORDERBY);
        theModel.setOrderBy(orderBy);
    }

}

