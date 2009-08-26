package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.jobs.model.ScheduledRepeatingJob;

public interface ScheduledGroupRequestExecutionDao {

	public void insert(ScheduledGroupRequestExecutionPair pair);
	
	public CommandRequestExecution findLatestCommandRequestExecutionForJobId(int jobId, Date cutoff);
	
	public List<ScheduledRepeatingJob> getJobs(int jobId, 
												Date startTime, 
												Date stopTime, 
												List<CommandRequestExecutionType> types, 
												ScheduleGroupRequestExecutionDaoEnabledFilter enabled, 
												ScheduleGroupRequestExecutionDaoPendingFilter pending,
												boolean acsending);
			
	public List<CommandRequestExecution> getCommandRequestExecutionsByJobId(int jobId, Date startTime, Date stopTime, boolean acsending);
	
	public int getCreCountByJobId(int jobId, Date startTime, Date stopTime);
	
}
