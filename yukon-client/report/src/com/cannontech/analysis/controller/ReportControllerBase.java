package com.cannontech.analysis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.BareReportModel;

public abstract class ReportControllerBase implements ReportController {
    protected BareReportModel model;
    protected YukonReportBase report;

    public Map<Integer,List<? extends Object>> getFilterObjectsMap() {
        HashMap<Integer, List<? extends Object>> result = new HashMap<Integer, List<? extends Object>>();
        if(getFilterModelTypes() == null) {
            return result;
        }else {
            for (Integer modelId : getFilterModelTypes()) {
                result.put(modelId, ReportFuncs.getObjectsByModelType(modelId));
            }
            return result;
        }
    }

    public int[] getFilterModelTypes() {
        return new int[] {};
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        
    }
}
