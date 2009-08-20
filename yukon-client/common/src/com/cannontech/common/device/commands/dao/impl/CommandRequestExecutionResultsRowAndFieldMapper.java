package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SqlUtils;

public class CommandRequestExecutionResultsRowAndFieldMapper implements RowAndFieldMapper<CommandRequestExecutionResult> {

	public Number getPrimaryKey(CommandRequestExecutionResult commandRequestExecutionResult) {
        return commandRequestExecutionResult.getId();
    }
    public void setPrimaryKey(CommandRequestExecutionResult commandRequestExecutionResult, int value) {
    	commandRequestExecutionResult.setId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, CommandRequestExecutionResult commandRequestExecutionResult) {
        p.addValue("CommandRequestExecId", commandRequestExecutionResult.getCommandRequestExecutionId());
        p.addValue("Command", commandRequestExecutionResult.getCommand());
        p.addValue("ErrorCode", commandRequestExecutionResult.getErrorCode());
        p.addValue("CompleteTime", commandRequestExecutionResult.getCompleteTime());
        p.addValue("DeviceId", commandRequestExecutionResult.getDeviceId());
        p.addValue("RouteId", commandRequestExecutionResult.getRouteId());
    }
	
    public CommandRequestExecutionResult mapRow(ResultSet rs, int rowNum) throws SQLException {
    	CommandRequestExecutionResult commandRequestExecutionResult = new CommandRequestExecutionResult();
    	commandRequestExecutionResult.setId(rs.getInt("CommandRequestExecResultId"));
    	commandRequestExecutionResult.setCommandRequestExecutionId(rs.getInt("CommandRequestExecId"));
    	commandRequestExecutionResult.setCommand(rs.getString("Command"));
    	commandRequestExecutionResult.setErrorCode(rs.getInt("ErrorCode"));
    	commandRequestExecutionResult.setCompleteTime(rs.getTimestamp("CompleteTime"));
    	commandRequestExecutionResult.setDeviceId(SqlUtils.getNullableInt(rs, "DeviceId"));
    	commandRequestExecutionResult.setRouteId(SqlUtils.getNullableInt(rs, "RouteId"));
    	return commandRequestExecutionResult;
    }
}
