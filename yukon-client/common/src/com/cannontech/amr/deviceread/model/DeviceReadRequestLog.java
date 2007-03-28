package com.cannontech.amr.deviceread.model;

import java.util.GregorianCalendar;

public class DeviceReadRequestLog {

	private Integer deviceReadRequestLogID;
	private Integer requestID;
	private String command;
	private GregorianCalendar startTime;
	private GregorianCalendar stopTime;
	private Integer deviceReadJobLogID;
	
	public DeviceReadRequestLog(Integer deviceReadRequestLogID, Integer requestID, String command, GregorianCalendar startTime, GregorianCalendar stopTime, Integer deviceReadJobLogID) {
		super();
		this.deviceReadRequestLogID = deviceReadRequestLogID;
		this.requestID = requestID;
		this.command = command;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.deviceReadJobLogID = deviceReadJobLogID;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Integer getDeviceReadJobLogID() {
		return deviceReadJobLogID;
	}
	public void setDeviceReadJobLogID(Integer deviceReadJobLogID) {
		this.deviceReadJobLogID = deviceReadJobLogID;
	}
	public Integer getDeviceReadRequestLogID() {
		return deviceReadRequestLogID;
	}
	public void setDeviceReadRequestLogID(Integer deviceReadRequestLogID) {
		this.deviceReadRequestLogID = deviceReadRequestLogID;
	}
	public Integer getRequestID() {
		return requestID;
	}
	public void setRequestID(Integer requestID) {
		this.requestID = requestID;
	}
	public GregorianCalendar getStartTime() {
		return startTime;
	}
	public void setStartTime(GregorianCalendar startTime) {
		this.startTime = startTime;
	}
	public GregorianCalendar getStopTime() {
		return stopTime;
	}
	public void setStopTime(GregorianCalendar stopTime) {
		this.stopTime = stopTime;
	}
	
	
}
