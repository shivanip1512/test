package com.cannontech.analysis.controller;

import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.DisconnectCollarReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DisconnectCollarModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.ServletRequestEnumUtils;

public class DisconnectCollarController extends ReportControllerBase {
    
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.METER,
            ReportFilter.DEVICE
            };
    
    public DisconnectCollarController() {
        super();
        model = (DisconnectCollarModel)YukonSpringHook.getBean("disconnectCollarModel");
        report = new DisconnectCollarReport(model);
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
        DisconnectCollarModel diconnectModel = (DisconnectCollarModel) model;
        super.setRequestParameters(request);

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.METER) {
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
            diconnectModel.setDeviceNames(null);
            diconnectModel.setDeviceIds(idsSet);
        }else if (filter == ReportFilter.DEVICE) {
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
            diconnectModel.setDeviceIds(null);
            diconnectModel.setDeviceNames(devicesSet);
        }
    }
}
