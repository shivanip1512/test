package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.LMControlSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LMControlSummaryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

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
        LMControlSummaryModel lmControlSummaryModel = (LMControlSummaryModel) model;
        super.setRequestParameters(request);
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        if (filterModelType == ReportFilter.PROGRAM.ordinal()) {
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_MODEL_VALUES).trim();
            StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
            if (filterModelType == ReportFilter.PROGRAM.ordinal()) {
                int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
                HashSet<Integer> programsSet = new HashSet<Integer>();
                for (int id : idsArray) {
                    programsSet.add(id);
                }
                lmControlSummaryModel.setProgramIds(programsSet);
            }
        }
    }
}
