package com.cannontech.common.device.commands.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;

public interface CommandRequestExecutionDao {

	public void saveOrUpdate(CommandRequestExecution commandRequestExecution);
	
	public CommandRequestExecution getById(int commandRequestExecutionId);
	
	public int getRequestCountByCreId(int commandRequestExecutionId);
	
	public boolean isComplete(int commandRequestExecutionId);
	
	public Date getStopTime(int commandRequestExecutionId);
	
	public List<CommandRequestExecution> findByRange(int commandRequestExecutionId, Date startTime, Date stopTime, DeviceRequestType type, boolean acsending);
	
}
