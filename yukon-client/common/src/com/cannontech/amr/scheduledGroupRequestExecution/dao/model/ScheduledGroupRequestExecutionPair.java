package com.cannontech.amr.scheduledGroupRequestExecution.dao.model;

import com.cannontech.common.device.commands.CommandRequestExecutionContextId;

public class ScheduledGroupRequestExecutionPair {

	private CommandRequestExecutionContextId commandRequestExecutionContextId;
	private int jobId;
	
	public CommandRequestExecutionContextId getCommandRequestExecutionContextId() {
		return commandRequestExecutionContextId;
	}
	public void setCommandRequestExecutionContextId(CommandRequestExecutionContextId commandRequestExecutionContextId) {
		this.commandRequestExecutionContextId = commandRequestExecutionContextId;
	}
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
}
