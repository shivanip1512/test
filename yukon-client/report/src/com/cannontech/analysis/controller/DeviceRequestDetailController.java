package com.cannontech.analysis.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.DeviceRequestDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DeviceRequestDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.spring.YukonSpringHook;

public class DeviceRequestDetailController extends ReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE};
    
    public DeviceRequestDetailController() {
        super();
        model = YukonSpringHook.getBean("deviceRequestDetailModel", DeviceRequestDetailModel.class);
        report = new DeviceRequestDetailReport(model);
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<DeviceRequestDetailModel> getModel() {
        return report.getModel();
    }

    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        DeviceRequestDetailModel deviceRequestDetailModel = (DeviceRequestDetailModel)model;
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.GROUPS.ordinal()) {
            String names[] = ServletRequestUtils.getStringParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            deviceRequestDetailModel.setGroupsFilter(namesList);
            deviceRequestDetailModel.setDeviceFilter(null);
        } else if(filterModelType == ReportFilter.DEVICE.ordinal()){
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_DEVICE_VALUES).trim();
            String names[] = filterValueList.split(", ");
            List<String> namesList = Arrays.asList(names); 
            deviceRequestDetailModel.setGroupsFilter(null);
            deviceRequestDetailModel.setDeviceFilter(namesList);
        }
        
    }
    
    public String getHTMLOptionsTable() {
        return "";
    }
}