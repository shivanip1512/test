package com.cannontech.web.amr.scheduledGroupRequestExecution;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagUtils;

public class ScheduledGroupRequestExecutionJobWrapperFactory {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private JobStatusDao jobStatusDao;
	private JobManager jobManager;
	
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime) {
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, scheduledGroupRequestExecutionDao, jobStatusDao, jobManager);
	}
	
	public class ScheduledGroupRequestExecutionJobWrapper {

		private ScheduledRepeatingJob job;
		private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
		private JobStatusDao jobStatusDao;
		private JobManager jobManager;
		
		private ScheduledGroupRequestExecutionTask task;
		private int creCount;
		
		public ScheduledGroupRequestExecutionJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime,
							ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao,
							JobStatusDao jobStatusDao,
							JobManager jobManager) {
			
			this.job = job;
			this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
			this.jobStatusDao = jobStatusDao;
			this.jobManager = jobManager;
			
			this.task = (ScheduledGroupRequestExecutionTask)this.jobManager.instantiateTask(this.job);
	        this.creCount = this.scheduledGroupRequestExecutionDao.getCreCountByJobId(this.job.getId(), startTime, stopTime);
		}
		
		public ScheduledRepeatingJob getJob() {
			return job;
		}
		
		public String getCommandRequestTypeShortName() {
			return this.task.getCommandRequestExecutionType().getShortName();
		}
		
		public Date getLastRun() {
			return this.jobStatusDao.getJobLastSuccessfulRunDate(this.job.getId());
		}
		
		public Date getNextRun() {
			
			Date nextRun = null;
			try {
				nextRun = this.jobManager.getNextRuntime(job, new Date());
			} catch (ScheduleException e) {
			}
			
			return nextRun;
		}
		
		public String getCommand() {
			return this.task.getCommand();
		}
		
		public Attribute getAttribute() {
			return this.task.getAttribute();
		}
		
		public String getDeviceGroupName() {
			return this.task.getGroupName();
		}
		
		public int getCreCount() {
			return creCount;
		}
		
		public String getScheduleDescription() {
			return CronExpressionTagUtils.getDescription(this.job.getCronString());
		}
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setJobStatusDao(JobStatusDao jobStatusDao) {
		this.jobStatusDao = jobStatusDao;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
}
