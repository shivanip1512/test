package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupExecutionCounts;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.jobs.model.ScheduledRepeatingJob;

public interface ScheduledGroupRequestExecutionDao {

	public void insert(ScheduledGroupRequestExecutionPair pair);
	
	public CommandRequestExecution findLatestCommandRequestExecutionForJobId(int jobId, Date cutoff);
	
	public List<ScheduledRepeatingJob> getJobs(int jobId, 
												Date startTime, 
												Date stopTime, 
												List<DeviceRequestType> types, 
												ScheduleGroupRequestExecutionDaoEnabledFilter enabled, 
												ScheduleGroupRequestExecutionDaoPendingFilter pending,
												ScheduleGroupRequestExecutionDaoOnetimeFilter onetime,
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
     * This query gives you the success, fail, and total counts for any given jobId. 
     * It is counting up failures and successes from the CommandRequestExecResult table (by looking at
     * whether or not the execution's ErrorCode was zero) and is getting the max RequestCount (total)
     * from the CommandRequestExec table. Getting the max like this is needed because we are joining 
     * the CRE table with the CRER table and hence have not one row to select the RequestCount from,
     * but many rows. Using the max function is the way we chose to get that value here instead of
     * writing a separate query to get that value on its own.
	 */
	public ScheduledGroupExecutionCounts getExecutionCountsForJobId(int jobId);

	/**
	 * Returns ScheduledGroupRequestExecutionStatus for given jobId
	 * @param jobId
	 * @return
	 */
	public ScheduledGroupRequestExecutionStatus getStatusByJobId(int jobId);
}
