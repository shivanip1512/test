package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlStateComparisonReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlStateComparisonModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;

public class CapControlStateComparisonController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String ATT_SHOW_MISMATCH = "showMisMatch";
    
    public CapControlStateComparisonController() {
        super();
        model = new CapControlStateComparisonModel();
        report = new CapControlStateComparisonReport(model);
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
        html += "          <td><input type='checkbox' name='" + ATT_SHOW_MISMATCH+"' value='true'> Show Only Mismatched States";
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        html += "      </table>" + LINE_SEPARATOR;
        html += "    </td>" + LINE_SEPARATOR;
        html += "  </tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        return html;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlStateComparisonModel stateComparisonModel = (CapControlStateComparisonModel) model;
        super.setRequestParameters(request);
        
        String showMisMatch = request.getParameter(ATT_SHOW_MISMATCH);
        if( showMisMatch != null) {
            stateComparisonModel.setShowMisMatch((showMisMatch.equalsIgnoreCase("true")?true:false));
        } else { 
            stateComparisonModel.setShowMisMatch(false);
        }
    }

}
