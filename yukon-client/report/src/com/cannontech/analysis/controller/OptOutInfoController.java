package com.cannontech.analysis.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.OptOutInfoReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.OptOutInfoModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.common.util.StringUtils;
import com.google.common.collect.Sets;

public class OptOutInfoController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.PROGRAM
            };
    
    public OptOutInfoController() {
        super();
        model = new OptOutInfoModel();
        report = new OptOutInfoReport(model);
    }

    public String getHTMLOptionsTable() {
        return "   <table width=\'100%\' border=\'0\' cellspacing=\'0\' cellpadding=\'0\' class=\'TableCell\'>\r\n" + 
               "        <tr valign=\"top\">\r\n" + 
               "          <td width=\"110px\">Account Number: </td>\r\n" +
               "          <td><textarea rows=\"5\" name=\"accountNumbers\" ></textarea></td>\r\n" + 
               "        </tr>\r\n" + 
               "      </table>\r\n" +
               " * If account numbers are supplied, programs selected below are ignored.<br>* If no account numbers are supplied, you must select one or more programs below.";
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
        optOutInfoModel.setAccountNumbers(request.getParameter("accountNumbers"));
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        if (filterModelType == ReportFilter.PROGRAM.ordinal()) {
        	
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
