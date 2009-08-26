package com.cannontech.common.device.commands.dao;

import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;

public enum CommandRequestExecutionResultsFilterType {

	SUCCESS("Success", new SimpleSqlFragment("ErrorCode = 0")),
	FAIL("Fail", new SimpleSqlFragment("ErrorCode > 0")),
	ALL("All", new SimpleSqlFragment("1 = 1"));
	
	private String description;
	private SqlFragmentSource sqlFragmentSource;
	
	CommandRequestExecutionResultsFilterType(String description, SqlFragmentSource sqlFragmentSource) {
		this.description = description;
		this.sqlFragmentSource = sqlFragmentSource;
	}
	
	public String getDescription() {
		return description;
	}
	
	public SqlFragmentSource getConditionSqlFragmentSource() {
		return sqlFragmentSource;
	}
	
	
}