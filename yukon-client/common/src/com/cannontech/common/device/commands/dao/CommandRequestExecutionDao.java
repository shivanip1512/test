package com.cannontech.common.device.commands.dao;

import java.util.Date;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandRequestExecutionDao {

	public void saveOrUpdate(CommandRequestExecution commandRequestExecution);
	
	public CommandRequestExecution getById(int commandRequestExecutionId);
	
	public int getRequestCountByCreId(int commandRequestExecutionId);
	
	public boolean isComplete(int commandRequestExecutionId);
	
	public Date getStopTime(int commandRequestExecutionId);
	
	/**
	 * Creates an execution with execution status of started.
	 */
    CommandRequestExecution createStartedExecution(CommandRequestType commandType, DeviceRequestType deviceType,
                                                   int requestCount, LiteYukonUser user);

    int getByRangeCount(int jobId, Instant from, Instant to, DeviceRequestType type);

    List<CommandRequestExecution> findByRange(PagingParameters paging, int jobId, Instant from, Instant to,
                                              DeviceRequestType type);
}
