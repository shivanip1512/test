package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;

public class ScheduledGroupRequestExecutionServiceImpl implements ScheduledGroupRequestExecutionService {

	private JobManager jobManager;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
    private DeviceGroupService deviceGroupService;
    
    private Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionServiceImpl.class);
    
    // SCHEDULE - COMMAND
	@Override
    public YukonJob schedule(String name, 
                             String groupName, 
                             String command, 
                             DeviceRequestType type, 
                             String cronExpression, 
                             YukonUserContext userContext,
                             RetryStrategy retryStrategy) {
        
        return doSchedule(name, groupName, command, null, type, cronExpression, userContext, retryStrategy);
    }
	
	// SCHEDULE - ATTRIBUTE
    @Override
    public YukonJob schedule(String name, 
                             String groupName, 
                             Set<? extends Attribute> attributes, 
                             DeviceRequestType type, 
                             String cronExpression, 
                             YukonUserContext userContext,
                             RetryStrategy retryStrategy) {
        
        return doSchedule(name, groupName, null, attributes, type, cronExpression, userContext, retryStrategy);
    }
	
    // MAKE TASK, SCHEDULE TASK
	private YukonJob doSchedule(String name, 
	                          String groupName, 
	                          String command, 
	                          Set<? extends Attribute> attributes, 
	                          DeviceRequestType type, 
	                          String cronExpression, 
	                          YukonUserContext userContext,
	                          RetryStrategy retryStrategy) {
	    
	    int retryCount = retryStrategy.getRetryCount();
	    Integer stopRetryAfterHoursCount = retryStrategy.getStopRetryAfterHoursCount();
	    Integer turnOffQueuingAfterRetryCount = retryStrategy.getTurnOffQueuingAfterRetryCount();

		ScheduledGroupRequestExecutionTask task = scheduledGroupRequestExecutionJobDefinition.createBean();
		task.setName(name);
    	task.setDeviceGroup(deviceGroupService.resolveGroupName(groupName));
    	task.setAttributes(attributes);
    	task.setCommand(command);
    	task.setCommandRequestExecutionType(type);
    	task.setRetryCount(retryCount);
    	task.setStopRetryAfterHoursCount(stopRetryAfterHoursCount);
    	task.setTurnOffQueuingAfterRetryCount(turnOffQueuingAfterRetryCount);

    	YukonJob job =  jobManager.scheduleJob(scheduledGroupRequestExecutionJobDefinition, task, cronExpression, userContext);
        
    	log.info("Job scheduled. jobId=" + job.getId() + ", groupName=" + groupName + ", attributes=" + attributes + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() +
    	         ", retryCount=" + retryCount + ", stopRetryAfterHoursCount=" + stopRetryAfterHoursCount + ", turnOffQueuingAfterRetryCount=" + turnOffQueuingAfterRetryCount + ".");
    	
        return job;
	}
	
	// SCHEDULE REPLACEMENT - COMMAND
	@Override
    public YukonJob scheduleReplacement(int existingJobId, 
                                        String name, 
                                        String groupName, 
                                        String command, 
                                        DeviceRequestType type, 
                                        String cronExpression, 
                                        YukonUserContext userContext,
                                        RetryStrategy retryStrategy) {
        
        return doScheduleReplacement(existingJobId, name, groupName, command, null, type, cronExpression, userContext, retryStrategy);
    }
	
	// SCHEDULE REPLACEMENT - ATTRIBUTE
	@Override
    public YukonJob scheduleReplacement(int existingJobId, 
                                        String name, 
                                        String groupName, 
                                        Set<? extends Attribute> attributes, 
                                        DeviceRequestType type, 
                                        String cronExpression, 
                                        YukonUserContext userContext,
                                        RetryStrategy retryStrategy) {
        
        return doScheduleReplacement(existingJobId, name, groupName, null, attributes, type, cronExpression, userContext, retryStrategy);
    }
	
	private YukonJob doScheduleReplacement(int existingJobId, 
	                                     String name, 
	                                     String groupName, 
	                                     String command, 
	                                     Set<? extends Attribute> attributes, 
	                                     DeviceRequestType type, 
	                                     String cronExpression, 
	                                     YukonUserContext userContext,
	                                     RetryStrategy retryStrategy) {
	
		// get current job, generate task
		ScheduledRepeatingJob existingJob = (ScheduledRepeatingJob)jobManager.getJob(existingJobId);
        	
		// delete old job
    	jobManager.deleteJob(existingJob);
    	
    	// schedule new job
    	YukonJob replacementJob = doSchedule(name, groupName, command, attributes, type, cronExpression, userContext, retryStrategy);
    	log.info("Job replaced. old jobId=" + existingJob.getId() + " deleted, replacement jobId=" + replacementJob.getId() + ", name=" + name + ", groupName=" + groupName + ", attribute=" + attributes + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + 
    	         ", retryCount=" + retryStrategy.getRetryCount() + ", stopRetryAfterHoursCount=" + retryStrategy.getStopRetryAfterHoursCount() + ", turnOffQueuingAfterRetryCount=" + retryStrategy.getTurnOffQueuingAfterRetryCount() + ".");
    	
		return replacementJob;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Resource(name="scheduledGroupRequestExecutionJobDefinition")
	public void setScheduledGroupRequestExecutionjobDefinition(
			YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
		this.scheduledGroupRequestExecutionJobDefinition = scheduledGroupRequestExecutionJobDefinition;
	}
	
	@Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}
