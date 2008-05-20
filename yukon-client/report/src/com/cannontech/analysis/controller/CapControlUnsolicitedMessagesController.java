package com.cannontech.analysis.controller;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.CapControlUnsolicitedMessagesReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.CapControlUnsolicitedMessagesModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public class CapControlUnsolicitedMessagesController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPBANK,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
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
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        CapControlUnsolicitedMessagesModel theModel = (CapControlUnsolicitedMessagesModel) model;
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
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
        } 
        
        String orderBy = request.getParameter(ATT_ORDERBY);
        theModel.setOrderBy(orderBy);
    }

}

