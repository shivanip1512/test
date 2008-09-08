package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.StrategyAssignmentReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.StrategyAssignmentModel;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.spring.YukonSpringHook;

public class StrategyAssignmentController extends CapControlReportControllerBase {

    private ReportFilter[] filterModelTypes = new ReportFilter[] {
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER};

    public StrategyAssignmentController() {
        super();
        model = (StrategyAssignmentModel) YukonSpringHook.getBean("strategyAssignmentModel");
        report = new StrategyAssignmentReport(model);
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
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBBUS.ordinal()) {
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
        }
    }

    public String getHTMLOptionsTable() {
        return "";
    }

    public LinkedHashMap<ReportFilter, List<? extends Object>> getFilterObjectsMap() {
        LinkedHashMap<ReportFilter, List<? extends Object>> result = new LinkedHashMap<ReportFilter, List<? extends Object>>();
        return result;
    }
}
