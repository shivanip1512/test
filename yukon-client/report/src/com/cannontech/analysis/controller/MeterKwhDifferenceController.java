package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.MeterKwhDifferenceReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MeterKwhDifferenceModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.util.ServletUtil;

public class MeterKwhDifferenceController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.METER,
            ReportFilter.COLLECTIONGROUP};
    
    private TimeZone timeZone = TimeZone.getDefault();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    
    public MeterKwhDifferenceController() {
        super();
        model = new MeterKwhDifferenceModel();
        report = new MeterKwhDifferenceReport(model);
    }

    public String getHTMLOptionsTable() {
        String html = "";
        
    return html;
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
    
    public void setRequestParameters(HttpServletRequest request) {
        
        MeterKwhDifferenceModel kwhModel = (MeterKwhDifferenceModel) model;
        super.setRequestParameters(request);
        int idsArray[] = ServletRequestUtils.getIntParameters(request, ReportModelBase.ATT_FILTER_METER_VALUES);
        HashSet<Integer> idsSet = new HashSet<Integer>();
        for (int id : idsArray) {
            idsSet.add(id);
        }
        
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.METER.ordinal()) {
            kwhModel.setGroupIdsFilter(null);
            kwhModel.setDeviceIdsFilter(idsSet);
        } else if (filterModelType == ReportFilter.COLLECTIONGROUP.ordinal()) {
            kwhModel.setDeviceIdsFilter(null);
            kwhModel.setGroupIdsFilter(idsSet);
        } 
        
        String param = request.getParameter(ReportModelBase.ATT_START_DATE);
        if( param != null) {
            ((MeterKwhDifferenceModel)model).setStartDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        } else {
            ((MeterKwhDifferenceModel)model).setStartDate(null);
        }
        
        param = request.getParameter(ReportModelBase.ATT_STOP_DATE);
        if( param != null) {
            ((MeterKwhDifferenceModel)model).setStopDate(ServletUtil.parseDateStringLiberally(param, timeZone));
        }else {
            ((MeterKwhDifferenceModel)model).setStopDate(null);
        }
    }
}
