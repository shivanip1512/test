package com.cannontech.analysis.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.LCR3102DataReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.LCR3102DataModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.spring.YukonSpringHook;

public class LCR3102DataController extends ReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE};
    
    public LCR3102DataController() {
        super();
        model = (LCR3102DataModel) YukonSpringHook.getBean("lcr3102DataModel");
        report = new LCR3102DataReport(model);
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
        super.setRequestParameters(request);
        LCR3102DataModel lcrModel = (LCR3102DataModel)model; 
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.GROUPS.ordinal()) {
            String names[] = ServletRequestUtils.getStringParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            lcrModel.setGroupsFilter(namesList);
            lcrModel.setDeviceFilter(null);
        } else if(filterModelType == ReportFilter.DEVICE.ordinal()){
            String filterValueList = request.getParameter(ReportModelBase.ATT_FILTER_DEVICE_VALUES).trim();
            String names[] = filterValueList.split(", ");
            List<String> namesList = Arrays.asList(names); 
            lcrModel.setGroupsFilter(null);
            lcrModel.setDeviceFilter(namesList);
        }
    }

    public String getHTMLOptionsTable() {
        return "";
    }

}