package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.VarImbalanceOnExecutionReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.VarImbalanceOnExecutionModel;
import com.cannontech.spring.YukonSpringHook;

public class VarImbalanceOnExecutionController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    public VarImbalanceOnExecutionController() {
        super();
        model = (VarImbalanceOnExecutionModel)YukonSpringHook.getBean("varImbalanceOnExecutionModel");
        report = new VarImbalanceOnExecutionReport(model);
    }
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String ATT_IMBALANCE = "imbalance";

    public String getHTMLOptionsTable() {
        String html = "";
        
        html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "  <tr>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td>kVAR Imbalance: <input type='text' name='" + ATT_IMBALANCE +"' value='500' size='3'>";
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        html += "      </table>" + LINE_SEPARATOR;
        html += "    </td>" + LINE_SEPARATOR;
        html += "  </tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        return html;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        VarImbalanceOnExecutionModel varImbalanceModel = (VarImbalanceOnExecutionModel) model;
        super.setRequestParameters(request);
        
        String imbalance = request.getParameter(ATT_IMBALANCE);
        if(imbalance != null) {
            Integer imbalanceValue = new Integer(request.getParameter(ATT_IMBALANCE));
            varImbalanceModel.setImbalance(imbalanceValue);
        }
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