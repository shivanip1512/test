package com.cannontech.common.device.commands.dao.model;

import java.util.Date;

import com.cannontech.common.device.commands.CommandRequestExecutionType;

public class CommandRequestExecution {

	private Integer id;
	private Date startTime;
	private Date stopTime;
	private int requestCount;
	private CommandRequestExecutionType type;
	private int userId;
	
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
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
