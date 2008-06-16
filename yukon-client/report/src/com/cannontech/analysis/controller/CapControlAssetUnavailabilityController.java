
package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.CapControlAssetUnavailabilityReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlAssetUnavailabilityModel;
import com.cannontech.analysis.tablemodel.CapControlFilterable;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.util.ServletUtil;

public class CapControlAssetUnavailabilityController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            };
    
    private TimeZone timeZone = TimeZone.getDefault();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public CapControlAssetUnavailabilityController() {
        super();
        model = new CapControlAssetUnavailabilityModel();
        report = new CapControlAssetUnavailabilityReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        return html;
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase getModel() {
        return report.getModel();
    }
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlFilterable filterableModel = (CapControlFilterable) model;
        super.setRequestParameters(request);
        int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
        HashSet<Integer> idsSet = new HashSet<Integer>();
        for (int id : idsArray) {
            idsSet.add(id);
        }

        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.CAPCONTROLFEEDER.ordinal()) {
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBSTATION.ordinal()) {
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setSubstationIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.CAPCONTROLSUBBUS.ordinal()) {
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setAreaIdsFilter(null);
            filterableModel.setSubbusIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.AREA.ordinal()) {
            filterableModel.setSubstationIdsFilter(null);
            filterableModel.setCapBankIdsFilter(null);
            filterableModel.setFeederIdsFilter(null);
            filterableModel.setSubbusIdsFilter(null);
            filterableModel.setAreaIdsFilter(idsSet);
        }
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null) {
            ((CapControlAssetUnavailabilityModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        } else {
            ((CapControlAssetUnavailabilityModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null) {
            ((CapControlAssetUnavailabilityModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((CapControlAssetUnavailabilityModel)model).setStopDate(null);
        }
    }
}
