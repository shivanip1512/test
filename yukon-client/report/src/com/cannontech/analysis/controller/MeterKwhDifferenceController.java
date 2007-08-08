package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.report.MeterKwhDifferenceReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MeterKwhDifferenceModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.util.ServletUtil;

public class MeterKwhDifferenceController extends ReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.METER,
            ReportFilter.DEVICE,
            ReportFilter.GROUPS};
    
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
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);
        if (filterModelType == ReportFilter.METER.ordinal()) {
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_METER_VALUES).trim();
            StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
            int[] idsArray = new int[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                String meterNumber = st.nextToken().trim();
                LiteYukonPAObject lPao = DaoFactory.getDeviceDao().getLiteYukonPaobjectByMeterNumber(meterNumber);
                if( lPao != null) {
                    idsArray[i++] = lPao.getYukonID();
                }
            }
            HashSet<Integer> idsSet = new HashSet<Integer>();
            for (int id : idsArray) {
                idsSet.add(id);
            }
            kwhModel.setGroupNames(null);
            kwhModel.setDeviceNamesFilter(null);
            kwhModel.setDeviceIdsFilter(idsSet);
        }else if (filterModelType == ReportFilter.DEVICE.ordinal()) {
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_DEVICE_VALUES).trim();
            StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
            int[] devicesArray = new int[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                String deviceName = st.nextToken().trim();
                LiteYukonPAObject lPao = DaoFactory.getDeviceDao().getLiteYukonPaobjectByDeviceName(deviceName);
                if( lPao != null) {
                    devicesArray[i++] = lPao.getYukonID();
                }
            }
            HashSet<Integer> devicesSet = new HashSet<Integer>();
            for (int id : devicesArray) {
                devicesSet.add(id);
            }
            kwhModel.setGroupNames(null);
            kwhModel.setDeviceIdsFilter(null);
            kwhModel.setDeviceNamesFilter(devicesSet);
        } else if ( filterModelType == ReportFilter.GROUPS.ordinal() )
		{
			String[] paramArray = request.getParameterValues(ReportModelBase.ATT_FILTER_MODEL_VALUES);
			if( paramArray != null) {
				HashSet<String> groupsSet = new HashSet<String>();
				for (String string : paramArray) {
					groupsSet.add(string);
				}
					
				//Unload paoIDs
				kwhModel.setDeviceIdsFilter(null);
				kwhModel.setDeviceNamesFilter(null);
				kwhModel.setGroupNames(groupsSet);
			}
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
