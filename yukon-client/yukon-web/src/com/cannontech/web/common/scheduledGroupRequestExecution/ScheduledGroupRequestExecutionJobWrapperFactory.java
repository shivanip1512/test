package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class ScheduledGroupRequestExecutionJobWrapperFactory {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private JobStatusDao jobStatusDao;
	private JobManager jobManager;
	private CronExpressionTagService cronExpressionTagService;
	
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext) {
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, userContext);
	}
	
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(int jobId, Date startTime, Date stopTime, YukonUserContext userContext) {
		ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, userContext);
	}
	
	public class ScheduledGroupRequestExecutionJobWrapper implements Comparable<ScheduledGroupRequestExecutionJobWrapper> {

		private ScheduledRepeatingJob job;
		private YukonUserContext userContext;
		
		private ScheduledGroupRequestExecutionTask task;
        private int creCount;
        private int latestRequestCount;

		public ScheduledGroupRequestExecutionJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext) {
			
			this.job = job;
			this.userContext = userContext;
			
			this.task = (ScheduledGroupRequestExecutionTask)jobManager.instantiateTask(this.job);
	        this.creCount = scheduledGroupRequestExecutionDao.getDistinctCreCountByJobId(this.job.getId(), startTime, stopTime);
            this.latestRequestCount = scheduledGroupRequestExecutionDao.getLatestRequestCountByJobId(this.job.getId());
		}
		
		public ScheduledRepeatingJob getJob() {
			return job;
		}
		public ScheduledGroupRequestExecutionTask getTask() {
			return this.task;
		}
		public String getCommandRequestTypeShortName() {
			return this.task.getCommandRequestExecutionType().getShortName();
		}
		
		public ScheduledGroupRequestExecutionStatus getJobStatus() {
			return scheduledGroupRequestExecutionDao.getStatusByJobId(this.job.getId());
		}
		
		public Date getLastRun() {
			return jobStatusDao.getJobLastSuccessfulRunDate(this.job.getId());
		}
		
		public Date getNextRun() {
			
			Date nextRun = null;
			try {
				nextRun = jobManager.getNextRuntime(job, new Date());
			} catch (ScheduleException e) {
			}
			
			return nextRun;
		}
		
		public String getCommand() {
			return this.task.getCommand();
		}
		
		public Set<? extends Attribute> getAttributes() {
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
		    if (this.task.getDeviceGroup() == null) {
		        return null;
		    }
			return this.task.getDeviceGroup().getFullName();
		}
		
		public boolean isRetrySetup() {
			return this.task.getRetryCount() > 0;
		}
		public Integer getQueuedRetryCount() {
			return this.task.getTurnOffQueuingAfterRetryCount();
		}
		public Integer getNonQueuedRetryCount() {
			int getTurnOffQueuingAfterRetryCount = this.task.getTurnOffQueuingAfterRetryCount() == null ? 0 : this.task.getTurnOffQueuingAfterRetryCount();
			return this.task.getRetryCount() - getTurnOffQueuingAfterRetryCount;
		}
		public Integer getStopRetryAfterHoursCount() {
			return this.task.getStopRetryAfterHoursCount();
		}
		
		public int getCreCount() {
			return creCount;
		}

        public int getLatestRequestCount() {
            this.latestRequestCount = scheduledGroupRequestExecutionDao.getLatestRequestCountByJobId(this.job.getId());
            return this.latestRequestCount;
        }

		public String getScheduleDescription() {
			return cronExpressionTagService.getDescription(this.job.getCronString(), this.userContext);
		}
		
		@Override
		public int compareTo(ScheduledGroupRequestExecutionJobWrapper o) {
			return this.task.getName().compareToIgnoreCase(o.getTask().getName());
		}
	}
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getNextRunComparator() {
		
		Ordering<Date> dateComparer = Ordering.natural().nullsLast();
		Ordering<String> normalStringComparer = Ordering.natural();

		Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNameOrdering = normalStringComparer
			.onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
				public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
					return from.getName();
				}
			});

		Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNextRunOrdering = dateComparer
			.onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, Date>() {
				public Date apply(ScheduledGroupRequestExecutionJobWrapper from) {
					return from.getNextRun();
				}
			});
		
		Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobNextRunOrdering.compound(jobNameOrdering);
		return result;
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
