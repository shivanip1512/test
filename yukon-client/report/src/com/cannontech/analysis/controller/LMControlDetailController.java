package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.LMControlDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LMControlDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

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
        LMControlDetailModel lmControlDetailModel = (LMControlDetailModel) model;
        super.setRequestParameters(request);
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        if (filterModelType == ReportFilter.PROGRAM.ordinal()) {
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_MODEL_VALUES).trim();
            StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
            int[] programsArray = new int[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                Integer programId = new Integer(st.nextToken().trim());
                if( programId != null && programId.intValue() > 0) {
                    programsArray[i++] = programId;
                }
            }
            HashSet<Integer> programsSet = new HashSet<Integer>();
            for (int id : programsArray) {
                programsSet.add(id);
            }
            lmControlDetailModel.setProgramIds(programsSet);
        }
    }
}
