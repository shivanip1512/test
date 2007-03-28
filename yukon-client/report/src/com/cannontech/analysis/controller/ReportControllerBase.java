package com.cannontech.analysis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;

public abstract class ReportControllerBase implements ReportController {
    protected BareReportModel model;
    protected YukonReportBase report;

    public Map<ReportFilter,List<? extends Object>> getFilterObjectsMap() {
        HashMap<ReportFilter, List<? extends Object>> result = new HashMap<ReportFilter, List<? extends Object>>();
        if(getFilterModelTypes() == null) {
            return result;
        }else {
            for (ReportFilter filter: getFilterModelTypes()) {
                result.put(filter, ReportFuncs.getObjectsByModelType(filter));
            }
            return result;
        }
    }

    public ReportFilter[] getFilterModelTypes() {
        return new ReportFilter[] {};
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        
    }
}
