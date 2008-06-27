package com.cannontech.analysis.controller;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.CapControlStateComparisonReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.CapControlStateComparisonModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

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
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        CapControlStateComparisonModel stateComparisonModel = (CapControlStateComparisonModel) model;
        super.setRequestParameters(request);
        int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
        HashSet<Integer> idsSet = new HashSet<Integer>();
        for (int id : idsArray) {
            idsSet.add(id);
        }

        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.CAPCONTROLFEEDER.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
        } else if (filterModelType == ReportFilter.CAPBANK.ordinal()) {
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setCapBankIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBBUS.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
        } else if (filterModelType == ReportFilter.AREA.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBSTATION.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
        }
        
        String showMisMatch = request.getParameter(ATT_SHOW_MISMATCH);
        if( showMisMatch != null) {
            stateComparisonModel.setShowMisMatch((showMisMatch.equalsIgnoreCase("true")?true:false));
        } else { 
            stateComparisonModel.setShowMisMatch(false);
        }
    }

}
