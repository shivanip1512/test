package com.cannontech.analysis.controller;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public abstract class CapControlReportControllerBase extends ReportControllerBase {
    @Override
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
            filterableModel.setFeederIdsFilter(idsSet);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        } else if (filterModelType == ReportFilter.CAPBANK.ordinal()) {
            filterableModel.setCapBankIdsFilter(idsSet);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBBUS.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        }else if (filterModelType == ReportFilter.CAPCONTROLSUBSTATION.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
            filterableModel.setAreaIdsFilter(null);
        } else if (filterModelType == ReportFilter.AREA.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(idsSet);
        }
    }
    
}
