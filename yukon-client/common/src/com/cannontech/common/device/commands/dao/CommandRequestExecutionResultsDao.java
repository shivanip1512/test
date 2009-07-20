package com.cannontech.common.device.commands.dao;

import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;

public interface CommandRequestExecutionResultsDao {

	public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult);
	
	public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId);
	
	public int getCountByExecutionId(int commandRequestExecutionId);
	public int getSucessCountByExecutionId(int commandRequestExecutionId);
	public int getFailCountByExecutionId(int commandRequestExecutionId);
	
	public List<Integer> getDeviceIdsByExecutionId(int commandRequestExecutionId);
	public List<Integer> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId);
	public List<Integer> getFailDeviceIdsByExecutionId(int commandRequestExecutionId);
	
	public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType);
}
