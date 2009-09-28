package com.cannontech.common.device.commands.dao.model;

import java.util.Date;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestType;

public class CommandRequestExecution {

	private Integer id;
	private int contextId;
	private Date startTime;
	private Date stopTime;
	private int requestCount;
	private CommandRequestExecutionType commandRequestExecutionType;
	private String userName;
	private CommandRequestType commandRequestType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getContextId() {
        return contextId;
    }
	public void setContextId(int contextId) {
        this.contextId = contextId;
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
	public CommandRequestExecutionType getCommandRequestExecutionType() {
		return commandRequestExecutionType;
	}
	public void setCommandRequestExecutionType(CommandRequestExecutionType commandRequestExecutionType) {
		this.commandRequestExecutionType = commandRequestExecutionType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public CommandRequestType getCommandRequestType() {
		return commandRequestType;
	}
	public void setCommandRequestType(CommandRequestType commandRequestType) {
		this.commandRequestType = commandRequestType;
	}
}
