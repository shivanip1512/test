package com.cannontech.analysis.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.OptOutLimitReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.OptOutLimitModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.util.StringUtils;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Sets;

public class OptOutLimitController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.ACCOUNT_NUMBER,
            ReportFilter.SERIAL_NUMBER,
            ReportFilter.PROGRAM,
            ReportFilter.USER,
            };
    
    public OptOutLimitController() {
        super();
        model = new OptOutLimitModel();
        report = new OptOutLimitReport(model);
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
        OptOutLimitModel optOutLimitModel = (OptOutLimitModel) model;
        EnergyCompanyModelAttributes ecModel = (EnergyCompanyModelAttributes)model;
        super.setRequestParameters(request);

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);
        
        if (filter == ReportFilter.ACCOUNT_NUMBER) {
            
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            Set<Integer> accountIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
            optOutLimitModel.setAccountIds(accountIdsSet);
        } else if (filter == ReportFilter.SERIAL_NUMBER) {
            
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            Set<Integer> inventoryIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
            optOutLimitModel.setInventoryIds(inventoryIdsSet);
        } else if (filter == ReportFilter.PROGRAM) {
            
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            Set<Integer> programIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
            optOutLimitModel.setProgramIds(programIdsSet);
        } else if (filter == ReportFilter.USER) {
            
            String filterValuesStr = ServletRequestUtils.getStringParameter(request, ReportModelBase.ATT_FILTER_MODEL_VALUES, "");
            Set<Integer> userIdsSet = Sets.newHashSet(StringUtils.parseIntStringForList(filterValuesStr));
            optOutLimitModel.setUserIds(userIdsSet);
        }
        
        String ecParam = request.getParameter("ecID");
        if (ecParam != null) {
            ecModel.setEnergyCompanyId(Integer.valueOf(ecParam));
        }
    }
}
