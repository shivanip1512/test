package com.cannontech.amr.deviceread.model;

import java.util.GregorianCalendar;

public class DeviceReadLog {

	private Integer deviceReadLogID;
	private Integer deviceID;
	private GregorianCalendar timestamp;
	private Short statusCode;
	private Integer deviceReadRequestLogID;
	
	public DeviceReadLog(Integer deviceReadLogID, Integer deviceID, GregorianCalendar timestamp, Short statusCode, Integer deviceReadRequestLogID) {
		super();
		this.deviceReadLogID = deviceReadLogID;
		this.deviceID = deviceID;
		this.timestamp = timestamp;
		this.statusCode = statusCode;
		this.deviceReadRequestLogID = deviceReadRequestLogID;
	}
	
	public Integer getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}
	public Integer getDeviceReadLogID() {
		return deviceReadLogID;
	}
	public void setDeviceReadLogID(Integer deviceReadLogID) {
		this.deviceReadLogID = deviceReadLogID;
	}
	public Integer getDeviceReadRequestLogID() {
		return deviceReadRequestLogID;
	}
	public void setDeviceReadRequestLogID(Integer deviceReadRequestLogID) {
		this.deviceReadRequestLogID = deviceReadRequestLogID;
	}
	public Short getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Short statusCode) {
		this.statusCode = statusCode;
	}
	public GregorianCalendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}
	
	
}