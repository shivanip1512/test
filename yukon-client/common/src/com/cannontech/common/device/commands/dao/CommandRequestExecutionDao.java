package com.cannontech.common.device.commands.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;

public interface CommandRequestExecutionDao {

	public void saveOrUpdate(CommandRequestExecution commandRequestExecution);
	
	public CommandRequestExecution getById(int commandRequestExecutionId);
	
	public CommandRequestExecution findLatest(Date cutoff);
	
	public int getRequestCountById(int commandRequestExecutionId);
	
	public boolean isComplete(int commandRequestExecutionId);
	
	public Date getStopTime(int commandRequestExecutionId);
	
	public List<CommandRequestExecution> findByRange(int commandRequestExecutionId, Date startTime, Date stopTime, CommandRequestExecutionType type, boolean acsending);
	
}
