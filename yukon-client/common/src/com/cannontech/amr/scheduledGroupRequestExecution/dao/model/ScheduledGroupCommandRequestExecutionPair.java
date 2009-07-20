package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

public class ScheduledGroupCommandRequestExecutionPair {

	private Integer commandRequestExecutionId;
	private int jobId;
	
	public int getCommandRequestExecutionId() {
		return commandRequestExecutionId;
	}
	public void setCommandRequestExecutionId(int commandRequestExecutionId) {
		this.commandRequestExecutionId = commandRequestExecutionId;
	}
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
}
