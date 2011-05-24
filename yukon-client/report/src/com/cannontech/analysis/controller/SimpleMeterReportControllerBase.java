package com.cannontech.analysis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.SimpleMeterReportModelBase;
import com.cannontech.web.util.ServletRequestEnumUtils;

public abstract class SimpleMeterReportControllerBase extends SimpleDeviceReportControllerBase {
    
	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE, ReportFilter.METER};
	
	@Override
	 public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
	
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);

		SimpleMeterReportModelBase<?> simpleMeterReportModel= (SimpleMeterReportModelBase<?>) model;
		//Clear out for super.
        simpleMeterReportModel.setMeterNumberFilter(null);

        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(req, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);
		if(filter == ReportFilter.METER){
	        String filterValueList = req.getParameter(ReportModelBase.ATT_FILTER_METER_VALUES).trim();
	        StringTokenizer st = new StringTokenizer(filterValueList, ",\t\n\r\f");
		    List<String> meterNumbers = new ArrayList<String>(st.countTokens());
		    while (st.hasMoreTokens()) {
		    	String token = st.nextToken().trim();
		    	if (StringUtils.isNotBlank(token)) {
		    		meterNumbers.add(token);
		    	}
		    }
		    simpleMeterReportModel.setGroupsFilter(null);
		    simpleMeterReportModel.setDeviceFilter(null);
	        simpleMeterReportModel.setMeterNumberFilter(meterNumbers);
    	}
	}
}
