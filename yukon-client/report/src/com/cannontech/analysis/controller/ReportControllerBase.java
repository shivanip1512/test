package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DatedModelAttributes;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.UserContextModelAttributes;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public abstract class ReportControllerBase implements ReportController {
    protected BareReportModel model = null;
    protected YukonReportBase report = null;
    
    public LinkedHashMap<ReportFilter,List<? extends Object>> getFilterObjectsMap(int userId) {
        LinkedHashMap<ReportFilter, List<? extends Object>> result = new LinkedHashMap<ReportFilter, List<? extends Object>>();
        if (getFilterModelTypes() == null) {
            return result;
        } else {
            for (ReportFilter filter : getFilterModelTypes()) {
                result.put(filter, ReportFuncs.getObjectsByModelType(filter, userId));
            }
            return result;
        }
    }
    
    public boolean reportHasFilter(int userId) {
        if (getFilterModelTypes() == null || getFilterModelTypes().length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public ReportFilter[] getFilterModelTypes() {
        return new ReportFilter[] {};
    }
    
    public void setRequestParameters(HttpServletRequest req) {
        if (model instanceof EnergyCompanyModelAttributes) {
            EnergyCompanyModelAttributes commonModel = (EnergyCompanyModelAttributes)model;
            commonModel.setEnergyCompanyId(report.getModel().getEnergyCompanyID());
        }
        
        if (model instanceof DatedModelAttributes) {
            DatedModelAttributes datedModel = (DatedModelAttributes) model;
            YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(req);
            TimeZone tz = yukonUserContext.getTimeZone();

            String param;
            param = req.getParameter("startDate");
            if ( param != null) {
                datedModel.setStartDate(ServletUtil.parseDateStringLiberally(param, tz));
            }

            param = req.getParameter("stopDate");
            if ( param != null) {
                datedModel.setStopDate(ServletUtil.parseDateStringLiberally(param, tz));
            }

        }
        
        if (model instanceof UserContextModelAttributes){
            UserContextModelAttributes commonModel = (UserContextModelAttributes)model;
            commonModel.setUserContext(YukonUserContextUtils.getYukonUserContext(req));
        }
    }
    
    @Override
    public boolean useStartStopTimes() {
        return false;
    }
    
    @Override
    public boolean supportsPdf() {
        return report.supportsPdf();
    }
}
