package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.database.RowAndFieldMapper;

public class CommandRequestExecutionRowAndFieldMapper implements RowAndFieldMapper<CommandRequestExecution> {

	public Number getPrimaryKey(CommandRequestExecution commandRequestExecution) {
        return commandRequestExecution.getId();
    }
	
	public void setPrimaryKey(CommandRequestExecution commandRequestExecution, int value) {
    	commandRequestExecution.setId(value);
    }
	
	public void extractValues(MapSqlParameterSource p, CommandRequestExecution commandRequestExecution) {
	    p.addValue("CommandRequestExecContextId", commandRequestExecution.getContextId());
	    p.addValue("StartTime", commandRequestExecution.getStartTime());
        p.addValue("StopTime", commandRequestExecution.getStopTime());
        p.addValue("RequestCount", commandRequestExecution.getRequestCount());
        p.addValue("CommandRequestExecType", commandRequestExecution.getCommandRequestExecutionType().name());
        p.addValue("UserName", commandRequestExecution.getUserName());
        p.addValue("CommandRequestType", commandRequestExecution.getCommandRequestType().name());
    }
	
	public CommandRequestExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
		CommandRequestExecution commandRequestExecution = new CommandRequestExecution();
		commandRequestExecution.setId(rs.getInt("CommandRequestExecId"));
		commandRequestExecution.setContextId(rs.getInt("CommandRequestExecContextId"));
    	commandRequestExecution.setStartTime(rs.getTimestamp("StartTime"));
    	commandRequestExecution.setStopTime(rs.getTimestamp("StopTime"));
    	commandRequestExecution.setRequestCount(rs.getInt("RequestCount"));
    	commandRequestExecution.setCommandRequestExecutionType(CommandRequestExecutionType.valueOf(rs.getString("CommandRequestExecType")));
    	commandRequestExecution.setUserName(rs.getString("UserName"));
    	commandRequestExecution.setCommandRequestType(CommandRequestType.valueOf(rs.getString("CommandRequestType")));
    	return commandRequestExecution;
	}
}
