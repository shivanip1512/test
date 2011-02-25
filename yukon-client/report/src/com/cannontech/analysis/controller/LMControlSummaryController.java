package com.cannontech.analysis.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.LMControlSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.LMControlSummaryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.util.StringUtils;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Sets;

public class LMControlSummaryController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.PROGRAM
            };
    
    public LMControlSummaryController() {
        super();
        model = new LMControlSummaryModel();
        report = new LMControlSummaryReport(model);
    }

    public String getHTMLOptionsTable() {
        return "* Select at least one program below.";
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

    public void setFilterModelTypes(ReportFilter[] filterModelTypes) {
        this.filterModelTypes = filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        LMControlSummaryModel lmControlSummaryModel = (LMControlSummaryModel) model;
        EnergyCompanyModelAttributes ecModel = (EnergyCompanyModelAttributes)model;
        super.setRequestParameters(request);

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.PROGRAM) {
            
        	String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
        	Set<Integer> paoIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
        	lmControlSummaryModel.setProgramIds(paoIdsSet);
        }
        String ecParam = request.getParameter("ecID");
        if (ecParam != null) {
            ecModel.setEnergyCompanyId(Integer.valueOf(ecParam));
        }
    }
}
