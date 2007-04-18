package com.cannontech.analysis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.EnergyCompanyModelAttributes;
import com.cannontech.analysis.tablemodel.DatedModelAttributes;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public abstract class ReportControllerBase implements ReportController {
    protected BareReportModel model = null;
    protected YukonReportBase report = null;;
    private EnergyCompanyDao energyCompanyDao = DaoFactory.getEnergyCompanyDao();
    
    public Map<ReportFilter,List<? extends Object>> getFilterObjectsMap() {
        HashMap<ReportFilter, List<? extends Object>> result = new HashMap<ReportFilter, List<? extends Object>>();
        if (getFilterModelTypes() == null) {
            return result;
        } else {
            for (ReportFilter filter : getFilterModelTypes()) {
                result.put(filter, ReportFuncs.getObjectsByModelType(filter));
            }
            return result;
        }
    }

    public ReportFilter[] getFilterModelTypes() {
        return new ReportFilter[] {};
    }
    
    public void setRequestParameters(HttpServletRequest req) {
        if (model instanceof EnergyCompanyModelAttributes) {
            EnergyCompanyModelAttributes commonModel = (EnergyCompanyModelAttributes)model;

            String param = req.getParameter("ecID");
            if ( param != null) {
                commonModel.setEnergyCompanyId(Integer.valueOf(param));
            }

        }
        
        if (model instanceof DatedModelAttributes) {
            DatedModelAttributes datedModel = (DatedModelAttributes) model;
            ServletUtil.getYukonUser(req);
            LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(req);
            LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(liteYukonUser);
            TimeZone tz = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);

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
    }
}
