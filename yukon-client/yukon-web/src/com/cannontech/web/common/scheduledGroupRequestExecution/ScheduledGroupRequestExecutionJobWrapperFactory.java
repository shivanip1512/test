package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.i18n.ObjectFormattingService;
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

    @Autowired private ScheduledGroupRequestExecutionStatusService executionStatusService;
	@Autowired private JobStatusDao jobStatusDao;
	@Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ObjectFormattingService objectFormattingService;
    private JobManager jobManager;
    
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext) {
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, userContext);
	}
	
	public ScheduledGroupRequestExecutionJobWrapper createJobWrapper(int jobId, Date startTime, Date stopTime, YukonUserContext userContext) {
		ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
		return new ScheduledGroupRequestExecutionJobWrapper(job, startTime, stopTime, userContext);
	}
	
	public class ScheduledGroupRequestExecutionJobWrapper implements Comparable<ScheduledGroupRequestExecutionJobWrapper> {

		private ScheduledRepeatingJob job;
		private ScheduledGroupRequestExecutionStatus jobStatus;
		private YukonUserContext userContext;
		
		private ScheduledGroupRequestExecutionTask task;

		public ScheduledGroupRequestExecutionJobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime, YukonUserContext userContext) {
			this.job = job;
			this.jobStatus = executionStatusService.getStatus(job.getId());
			this.userContext = userContext;
			this.task = (ScheduledGroupRequestExecutionTask)jobManager.instantiateTask(this.job);
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
			return this.jobStatus;
		}
		
		public Date getLastRun() {
			return jobStatusDao.findJobLastSuccessfulRunDate(this.job.getId());
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
		    String description;
		    for (Attribute attr : getAttributes()) {
		        description = objectFormattingService.formatObjectAsString(attr.getMessage(), userContext);
		        attrDescriptions.add(description);
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
		
		public String getScheduleDescription() {
			return cronExpressionTagService.getDescription(this.job.getCronString(), this.userContext);
		}
		
		@Override
		public int compareTo(ScheduledGroupRequestExecutionJobWrapper o) {
			return this.task.getName().compareToIgnoreCase(o.getTask().getName());
		}
	}
	
    public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getNextRunAndNameComparator() {
        Ordering<Date> dateComparer = Ordering.natural().nullsLast();
        Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNextRunOrdering = dateComparer
            .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, Date>() {
                @Override
                public Date apply(ScheduledGroupRequestExecutionJobWrapper from) {
                    return from.getNextRun();
                }
            });
        Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobNextRunOrdering.compound(getJobNameComparator());
        return result;
    }
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getJobNameComparator() {
		Ordering<String> normalStringComparer = Ordering.natural();
		Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNameOrdering = normalStringComparer
			.onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
				@Override
                public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
					return from.getName();
				}
			});
		return jobNameOrdering;
	}
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getDeviceGroupNameComparator() {
	    Ordering<String> normalStringComparer = Ordering.natural().nullsFirst();
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNameOrdering = normalStringComparer
	    .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
	        @Override
            public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
	            return from.getDeviceGroupName();
	        }
	    });
	    return jobNameOrdering;
	}
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getAttributeCommandComparator() {
	    Ordering<String> normalStringComparer = Ordering.natural();
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> jobAttributeCommandOrdering = normalStringComparer
	    .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
	        @Override
            public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
	            if (!CollectionUtils.isEmpty(from.getAttributes())) {
	                return from.getAttributeDescriptions();
	            }
	            return from.getCommand();
	        }
	    });
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobAttributeCommandOrdering.compound(getJobNameComparator());
	    return result;
	}
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getRunScheduleComparator() {
	    Ordering<String> normalStringComparer = Ordering.natural();
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> jobRunScheduleOrdering = normalStringComparer
	    .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
	        @Override
            public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
	            return from.getScheduleDescription();
	        }
	    });
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobRunScheduleOrdering.compound(getJobNameComparator());
	    return result;
	}
	
	public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getNextRunComparator() {
	    Ordering<Date> dateComparer = Ordering.natural().nullsLast();
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> jobNextRunOrdering = dateComparer
	    .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, Date>() {
	        @Override
            public Date apply(ScheduledGroupRequestExecutionJobWrapper from) {
	            return from.getNextRun();
	        }
	    });
	    Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobNextRunOrdering.compound(getJobNameComparator());
	    return result;
	}
	
    public static Comparator<ScheduledGroupRequestExecutionJobWrapper> getStatusComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<ScheduledGroupRequestExecutionJobWrapper> jobStatusOrdering = normalStringComparer
        .onResultOf(new Function<ScheduledGroupRequestExecutionJobWrapper, String>() {
            @Override
            public String apply(ScheduledGroupRequestExecutionJobWrapper from) {
                return from.getJobStatus().name();
            }
        });
        Ordering<ScheduledGroupRequestExecutionJobWrapper> result = jobStatusOrdering.compound(getJobNameComparator());
        return result;
    }
    
    @Resource(name="jobManager")
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }
    
}
