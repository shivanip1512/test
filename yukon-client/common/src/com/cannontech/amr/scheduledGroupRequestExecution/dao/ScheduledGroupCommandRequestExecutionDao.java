package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupCommandRequestExecutionPair;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.jobs.model.ScheduledRepeatingJob;

public interface ScheduledGroupCommandRequestExecutionDao {

	public void insert(ScheduledGroupCommandRequestExecutionPair pair);
	
	public CommandRequestExecution getLatestCommandRequestExecutionForJobId(int jobId, Date cutoff);
	
	public int getLatestRequestCountForJobId(int jobId);
	public int getLatestSuccessCountForJobId(int jobId);
	public int getLatestFailCountForJobId(int jobId);
	
	public List<ScheduledRepeatingJob> getJobs(int jobId, Date startTime, Date stopTime, CommandRequestExecutionType type, boolean acsending);
	
	public List<CommandRequestExecution> getCommandRequestExecutionsByJobId(int jobId, Date startTime, Date stopTime, boolean acsending);
	
	public int getCreCountByJobId(int jobId, Date startTime, Date stopTime);
	
}
