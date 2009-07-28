package com.cannontech.common.device.commands.dao.model;

import java.util.Date;


public class CommandRequestExecutionResult {

	private Integer id;
	private int commandRequestExecutionId;
	private String command;
	private int errorCode;
	private Date completeTime;
	private Integer deviceId;
	private Integer routeId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getCommandRequestExecutionId() {
		return commandRequestExecutionId;
	}
	public void setCommandRequestExecutionId(int commandRequestExecutionId) {
		this.commandRequestExecutionId = commandRequestExecutionId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public Date getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public Integer getRouteId() {
		return routeId;
	}
	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}
}
