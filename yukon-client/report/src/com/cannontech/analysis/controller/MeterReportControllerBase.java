package com.cannontech.analysis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.tablemodel.MeterReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.web.util.ServletRequestEnumUtils;

public abstract class MeterReportControllerBase extends DeviceReportControllerBase {
    
	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE, ReportFilter.METER};
	
	@Override
	 public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
	
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);
		
		MeterReportModelBase<?> meterReportModel= (MeterReportModelBase<?>) model;
        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(req, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

    	// GROUPS and DEVICE are handled by super.setRequestParameters
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
    	    meterReportModel.setGroupsFilter(null);
            meterReportModel.setDeviceFilter(null);
            meterReportModel.setMeterNumberFilter(meterNumbers);
        } else {
        	meterReportModel.setMeterNumberFilter(null);
        }
	}
}
