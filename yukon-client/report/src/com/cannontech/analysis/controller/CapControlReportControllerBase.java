package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.FilterObjectsReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.web.util.ServletRequestEnumUtils;

public abstract class CapControlReportControllerBase extends ReportControllerBase {
    
    @Override
    public LinkedHashMap<ReportFilter,List<? extends Object>> getFilterObjectsMap(int userId) {
        LinkedHashMap<ReportFilter, List<? extends Object>> result = new LinkedHashMap<ReportFilter, List<? extends Object>>();
        ReportFilter[] filterModelTypes = getFilterModelTypes();
        return FilterObjectsReportModelBase.getFilteredCapControlObjectsMap(userId, result, filterModelTypes);
    }
    
    @Override
    public void setRequestParameters(HttpServletRequest request) {
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        super.setRequestParameters(request);
        int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
        HashSet<Integer> idsSet = new HashSet<Integer>();
        for (int id : idsArray) {
            idsSet.add(id);
        }

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.CAPCONTROLFEEDER) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        } else if (filter == ReportFilter.CAPBANK) {
            filterableModel.setCapBankIdsFilter(idsSet);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        } else if (filter == ReportFilter.CAPCONTROLSUBBUS) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
        }else if (filter == ReportFilter.CAPCONTROLSUBSTATION) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
            filterableModel.setAreaIdsFilter(null);
        } else if (filter == ReportFilter.AREA) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setAreaIdsFilter(idsSet);
        } else if (filter == ReportFilter.STRATEGY) {
        	filterableModel.setStrategyIdsFilter(idsSet);
        }
    }
    
}
