package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ReportFilter;

public class FilteredModelHelper {

	private ReportFilter filter;
	private String deviceNameValues;
	private String meterNumberValues;
	private String[] groupNameValues;
	
	public FilteredModelHelper(ReportFilter filter) {
		super();
		this.filter = filter;
	}
	
	public FilteredModelHelper(ReportFilter filter, String[] groupNameValues, String deviceNameValues, String meterNumberValues) {
		super();
		this.filter = filter;
		this.groupNameValues = groupNameValues;
		this.deviceNameValues = deviceNameValues;
		this.meterNumberValues = meterNumberValues;
	}
	
	public ReportFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ReportFilter filter) {
		this.filter = filter;
	}
	
	public String getDeviceNameValues() {
		return deviceNameValues;
	}
	
	public void setDeviceNameValues(String deviceNameValues) {
		this.deviceNameValues = deviceNameValues;
	}
	
	public String getMeterNumberValues() {
		return meterNumberValues;
	}
	
	public void setMeterNumberValues(String meterNumberValues) {
		this.meterNumberValues = meterNumberValues;
	}
	
	public String[] getGroupNameValues() {
		return groupNameValues;
	}
	
	public void setGroupNameValues(String[] groupNameValues) {
		this.groupNameValues = groupNameValues;
	}
}
