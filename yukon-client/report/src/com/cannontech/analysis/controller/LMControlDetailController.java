package com.cannontech.analysis.controller;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.LMControlDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LMControlDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.util.ServletUtil;

public class LMControlDetailController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.PROGRAM
            };
    
    public LMControlDetailController() {
        super();
        model = new LMControlDetailModel();
        report = new LMControlDetailReport(model);
    }

    public String getHTMLOptionsTable() {
        return "   <table width=\'100%\' border=\'0\' cellspacing=\'0\' cellpadding=\'0\' class=\'TableCell\'>\r\n" + 
               "        <tr valign=\"top\">\r\n" + 
               "          <td width=\"110px\">Account Number: </td>\r\n" +
               "          <td><textarea rows=\"5\" name=\"accountNumbers\" ></textarea></td>\r\n" + 
               "        </tr>\r\n" + 
               "      </table>\r\n";
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
        LMControlDetailModel lmControlDetailModel = (LMControlDetailModel) model;
        super.setRequestParameters(request);
        lmControlDetailModel.setAccountNumbers(request.getParameter("accountNumbers"));
        lmControlDetailModel.setLiteUser(ServletUtil.getYukonUser(request));
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        if (filterModelType == ReportFilter.PROGRAM.ordinal()) {
            int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            HashSet<Integer> programsSet = new HashSet<Integer>();
            for (int id : idsArray) {
                programsSet.add(id);
            }
            lmControlDetailModel.setProgramIds(programsSet);
        }
    }
}
