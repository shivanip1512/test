package com.cannontech.common.device.commands.dao;

public enum CommandRequestExecutionResultsFilterType {

	SUCCESS("Success", "ErrorCode = 0"),
	FAIL("Fail", "ErrorCode > 0"),
	ALL("All", null);
	
	private String description;
	private String sqlCondition;
	
	CommandRequestExecutionResultsFilterType(String description, String sqlCondition) {
		this.description = description;
		this.sqlCondition = sqlCondition;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getSqlCondition() {
		return sqlCondition;
	}
}