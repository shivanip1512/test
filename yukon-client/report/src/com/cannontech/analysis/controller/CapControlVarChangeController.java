package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlVarChangeReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlVarChangeModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class CapControlVarChangeController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String ATT_QUERY_PERCENT = "queryPercent";
    
    public CapControlVarChangeController() {
        super();
        model = new CapControlVarChangeModel();
        report = new CapControlVarChangeReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td>Var Percent change greater than: <input type='text' name='" + ATT_QUERY_PERCENT +"' value='100' size='3'>";
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
        CapControlVarChangeModel varChangeModel = (CapControlVarChangeModel) model;
        super.setRequestParameters(request);
        
        String percent = request.getParameter(ATT_QUERY_PERCENT);
        if(percent != null) {
            Integer queryPercent = new Integer(request.getParameter(ATT_QUERY_PERCENT));
            varChangeModel.setQueryPercent(queryPercent);
        }
    }
    
}
