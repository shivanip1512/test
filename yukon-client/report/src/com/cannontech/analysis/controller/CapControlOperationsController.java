package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.CapControlOperationsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapBankOperationsPerformanceModel;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.util.ServletUtil;

public class CapControlOperationsController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.CAPCONTROLSUBSTATION,
    		ReportFilter.CAPCONTROLSUBBUS,
    		ReportFilter.CAPBANK,
    		ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.AREA};
    
    private TimeZone timeZone = TimeZone.getDefault();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
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
        html += "</table>" + LINE_SEPARATOR;
        return html;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        CapControlOperationsModel operationsModel = (CapControlOperationsModel) model;
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
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPBANK.ordinal()) {
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setCapBankIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBBUS.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBSTATION.ordinal()) {
            filterableModel.setSubstationIdsFilter(idsSet);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
        } else if (filterModelType == ReportFilter.AREA.ordinal()) {
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(idsSet);
        }
        
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
