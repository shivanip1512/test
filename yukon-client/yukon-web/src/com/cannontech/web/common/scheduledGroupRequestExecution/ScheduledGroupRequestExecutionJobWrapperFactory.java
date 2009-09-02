package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;

public class ScheduledGroupRequestExecutionJobWrapperFactory {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private JobStatusDao jobStatusDao;
	private JobManager jobManager;
	private CronExpressionTagService cronExpressionTagService;
	
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext) {
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, userContext, scheduledGroupRequestExecutionDao, jobStatusDao, jobManager);
	}
	
	public class ScheduledGroupRequestExecutionJobWrapper {

		private ScheduledRepeatingJob job;
		private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
		private JobStatusDao jobStatusDao;
		private JobManager jobManager;
		private YukonUserContext userContext;
		
		private ScheduledGroupRequestExecutionTask task;
		private int creCount;
		
		public ScheduledGroupRequestExecutionJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext,
							ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao,
							JobStatusDao jobStatusDao,
							JobManager jobManager) {
			
			this.job = job;
			this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
			this.jobStatusDao = jobStatusDao;
			this.jobManager = jobManager;
			this.userContext = userContext;
			
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
		
		public Set<Attribute> getAttributes() {
			return this.task.getAttributes();
		}
		public String getAttributeDescriptions() {
		    List<String> attrDescriptions = new ArrayList<String>();
		    for (Attribute attr : getAttributes()) {
		        attrDescriptions.add(attr.getDescription());
		    }
		    return StringUtils.join(attrDescriptions, ", ");
		}
		
		public String getName() {
			return this.task.getName();
		}
		
		public String getDeviceGroupName() {
			return this.task.getDeviceGroup().getFullName();
		}
		
		public int getCreCount() {
			return creCount;
		}
		
		public String getScheduleDescription() {
			return cronExpressionTagService.getDescription(this.job.getCronString(), this.userContext);
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
	
	@Autowired
	public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
}
