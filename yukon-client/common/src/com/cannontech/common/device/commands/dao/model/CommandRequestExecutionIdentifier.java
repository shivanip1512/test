package com.cannontech.common.device.commands.dao.model;

public class CommandRequestExecutionIdentifier {

	private int commandRequestExecutionId;
	
	public CommandRequestExecutionIdentifier(int id) {
		this.commandRequestExecutionId = id;
	}
	
	public int getCommandRequestExecutionId() {
		return commandRequestExecutionId;
	}
	
	public void setCommandRequestExecutionId(int commandRequestExecutionId) {
		this.commandRequestExecutionId = commandRequestExecutionId;
	}
}
