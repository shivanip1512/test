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
	
	/**
	 * Returns count of the the number of executions of this schedule. Executions are grouped by contextId
	 * such that retries are not counted as additional executions.
	 * Only those executions that occur within the startTime and stopTime are eligible to be counted as part of
	 * a distinct execution.
	 * @param jobId
	 * @param startTime
	 * @param stopTime
	 * @return
	 */
	public int getDistinctCreCountByJobId(int jobId, Date startTime, Date stopTime);

	/**
	 * Returns count of the number of failed requests of the most recent execution of the job.
	 * @param commandRequestExecutionContextId
	 * @return
	 */
	public int getLatestFailCountByJobId(int jobId);
	
	/**
	 * Returns count of the number of successful requests of the most recent execution of the job.
	 * @param commandRequestExecutionContextId
	 * @return
	 */
	public int getLatestSuccessCountByJobId(int jobId);
	
	/**
	 * Returns the request count of the most recent execution of this job. Retries that for the same contextId as the latest execution
	 * are not counted - the value of the original request is what is counted (the highest value cres for the context will ever have).
	 * @param jobId
	 * @return
	 */
	public int getLatestRequestCountByJobId(int jobId);
	
	/**
	 * Returns ScheduledGroupRequestExecutionStatus for given jobId
	 * @param jobId
	 * @return
	 */
	public ScheduledGroupRequestExecutionStatus getStatusByJobId(int jobId);
}
