package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.CapControlConfirmationPercentageReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlConfirmationPercentageModel;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.util.ServletUtil;

public class CapControlConfirmationPercentageController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    private TimeZone timeZone = TimeZone.getDefault();
    
    public CapControlConfirmationPercentageController() {
        super();
        model = new CapControlConfirmationPercentageModel();
        report = new CapControlConfirmationPercentageReport(model);
    }

    public String getHTMLOptionsTable() {
        return "";
    }

    public YukonReportBase getReport() {
        return report;
    }

    public ReportModelBase getModel() {
        return report.getModel();
    }
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlFilterable filterableModel = (CapControlFilterable) model;
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
            filterableModel.setAreaIdsFilter(idsSet);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
        }   else if (filterModelType == ReportFilter.CAPCONTROLSUBSTATION.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
        } 
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null)
            ((CapControlConfirmationPercentageModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        else
            ((CapControlConfirmationPercentageModel)model).setStartDate(null);
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null)
            ((CapControlConfirmationPercentageModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        else
            ((CapControlConfirmationPercentageModel)model).setStopDate(null);
    }

}
