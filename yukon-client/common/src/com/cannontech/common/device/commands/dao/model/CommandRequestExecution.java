package com.cannontech.common.device.commands.dao.model;

import java.util.Date;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestType;

public class CommandRequestExecution {

	private Integer id;
	private Date startTime;
	private Date stopTime;
	private int requestCount;
	private CommandRequestExecutionType type;
	private Integer userId;
	private CommandRequestType commandRequestType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getStopTime() {
		return stopTime;
	}
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	public int getRequestCount() {
		return requestCount;
	}
	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}
	public CommandRequestExecutionType getType() {
		return type;
	}
	public void setType(CommandRequestExecutionType type) {
		this.type = type;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public CommandRequestType getCommandRequestType() {
		return commandRequestType;
	}
	public void setCommandRequestType(CommandRequestType commandRequestType) {
		this.commandRequestType = commandRequestType;
	}
}
