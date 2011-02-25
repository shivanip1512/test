package com.cannontech.analysis.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.OptOutInfoReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.OptOutInfoModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.util.StringUtils;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Sets;

public class OptOutInfoController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.ACCOUNT_NUMBER,
            ReportFilter.PROGRAM,
            };
    
    public OptOutInfoController() {
        super();
        model = new OptOutInfoModel();
        report = new OptOutInfoReport(model);
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

    public void setFilterModelTypes(ReportFilter[] filterModelTypes) {
        this.filterModelTypes = filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        OptOutInfoModel optOutInfoModel = (OptOutInfoModel) model;
        EnergyCompanyModelAttributes ecModel = (EnergyCompanyModelAttributes)model;
        super.setRequestParameters(request);

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.ACCOUNT_NUMBER) {
            
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            Set<Integer> accountIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
            optOutInfoModel.setAccountIds(accountIdsSet);
        } else if (filter == ReportFilter.PROGRAM) {
        	
        	String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
        	Set<Integer> paoIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
        	optOutInfoModel.setProgramIds(paoIdsSet);
        }
        
        String ecParam = request.getParameter("ecID");
        if (ecParam != null) {
            ecModel.setEnergyCompanyId(Integer.valueOf(ecParam));
        }
    }
}
