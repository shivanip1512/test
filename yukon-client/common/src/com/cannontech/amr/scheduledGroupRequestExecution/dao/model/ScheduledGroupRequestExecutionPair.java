package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

public class ScheduledGroupRequestExecutionPair {

	private int commandRequestExecutionContextId;
	private int jobId;
	
	public int getCommandRequestExecutionContextId() {
		return commandRequestExecutionContextId;
	}
	public void setCommandRequestExecutionContextId(int commandRequestExecutionContextId) {
		this.commandRequestExecutionContextId = commandRequestExecutionContextId;
	}
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
}
