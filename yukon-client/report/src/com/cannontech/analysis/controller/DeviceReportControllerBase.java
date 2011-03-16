package com.cannontech.analysis.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.DeviceReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.web.util.ServletRequestEnumUtils;

public abstract class DeviceReportControllerBase extends ReportControllerBase {
    
	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE};
	
	@Override
	 public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
	
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);
		
		DeviceReportModelBase<?> deviceReportModel= (DeviceReportModelBase<?>) model;
        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(req, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);
		
        if (filter == ReportFilter.GROUPS) {
            String names[] = ServletRequestUtils.getStringParameters(req, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            deviceReportModel.setGroupsFilter(namesList);
            deviceReportModel.setDeviceFilter(null);
        } else if(filter == ReportFilter.DEVICE){
            String filterValueList = req.getParameter(ReportModelBase.ATT_FILTER_DEVICE_VALUES).trim();
            StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
    	    List<String> deviceNames = new ArrayList<String>(st.countTokens());
    	    while (st.hasMoreTokens()) {
    	    	String token = st.nextToken().trim();
    	    	if (StringUtils.isNotBlank(token)) {
    	    		deviceNames.add(token);
    	    	}
    	    }
    	    deviceReportModel.setGroupsFilter(null);
            deviceReportModel.setDeviceFilter(deviceNames);
        }
	}
}
