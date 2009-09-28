package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.groups.service.DeviceGroupService;
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
	public YukonJob schedule(String name, 
	                         String groupName, 
	                         String command, 
	                         CommandRequestExecutionType type, 
	                         String cronExpression, 
	                         YukonUserContext userContext) {
		
		return schedule(name, groupName, command, null, type, cronExpression, userContext, 0, null, null);
	}
	
	public YukonJob scheduleWithRetry(String name, 
                             String groupName, 
                             String command, 
                             CommandRequestExecutionType type, 
                             String cronExpression, 
                             YukonUserContext userContext,
                             int retryCount,
                             Integer stopRetryAfterHoursCount,
                             Integer turnOffQueuingAfterRetryCount) {
        
        return schedule(name, groupName, command, null, type, cronExpression, userContext, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
    }
	
	// SCHEDULE - ATTRIBUTE
	public YukonJob schedule(String name, 
	                         String groupName, 
	                         Set<? extends Attribute> attributes, 
	                         CommandRequestExecutionType type, 
	                         String cronExpression, 
	                         YukonUserContext userContext) {
		
		return schedule(name, groupName, null, attributes, type, cronExpression, userContext, 0, null, null);
	}
	
	// SCHEDULE - ATTRIBUTE
    public YukonJob scheduleWithRetry(String name, 
                             String groupName, 
                             Set<? extends Attribute> attributes, 
                             CommandRequestExecutionType type, 
                             String cronExpression, 
                             YukonUserContext userContext,
                             int retryCount,
                             Integer stopRetryAfterHoursCount,
                             Integer turnOffQueuingAfterRetryCount) {
        
        return schedule(name, groupName, null, attributes, type, cronExpression, userContext, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
    }
	
    // MAKE TASK, SCHEDULE TASK
	private YukonJob schedule(String name, 
	                          String groupName, 
	                          String command, 
	                          Set<? extends Attribute> attributes, 
	                          CommandRequestExecutionType type, 
	                          String cronExpression, 
	                          YukonUserContext userContext,
	                          int retryCount,
	                          Integer stopRetryAfterHoursCount,
	                          Integer turnOffQueuingAfterRetryCount) {

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
	public YukonJob scheduleReplacement(int existingJobId, 
	                                    String name, 
	                                    String groupName, 
	                                    String command, 
	                                    CommandRequestExecutionType type, 
	                                    String cronExpression, 
	                                    YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, name, groupName, command, null, type, cronExpression, userContext, 0, null, null);
	}
	
	public YukonJob scheduleReplacementWithRetry(int existingJobId, 
                                        String name, 
                                        String groupName, 
                                        String command, 
                                        CommandRequestExecutionType type, 
                                        String cronExpression, 
                                        YukonUserContext userContext,
                                        int retryCount,
                                        Integer stopRetryAfterHoursCount,
                                        Integer turnOffQueuingAfterRetryCount) {
        
        return scheduleReplacement(existingJobId, name, groupName, command, null, type, cronExpression, userContext, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
    }
	
	// SCHEDULE REPLACEMENT - ATTRIBUTE
	public YukonJob scheduleReplacement(int existingJobId, 
	                                    String name, 
	                                    String groupName, 
	                                    Set<? extends Attribute> attributes, 
	                                    CommandRequestExecutionType type, 
	                                    String cronExpression, 
	                                    YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, name, groupName, null, attributes, type, cronExpression, userContext, 0, null, null);
	}
	
	public YukonJob scheduleReplacementWithRetry(int existingJobId, 
                                        String name, 
                                        String groupName, 
                                        Set<? extends Attribute> attributes, 
                                        CommandRequestExecutionType type, 
                                        String cronExpression, 
                                        YukonUserContext userContext,
                                        int retryCount,
                                        Integer stopRetryAfterHoursCount,
                                        Integer turnOffQueuingAfterRetryCount) {
        
        return scheduleReplacement(existingJobId, name, groupName, null, attributes, type, cronExpression, userContext, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
    }
	
	private YukonJob scheduleReplacement(int existingJobId, 
	                                     String name, 
	                                     String groupName, 
	                                     String command, 
	                                     Set<? extends Attribute> attributes, 
	                                     CommandRequestExecutionType type, 
	                                     String cronExpression, 
	                                     YukonUserContext userContext,
	                                     int retryCount,
	                                     Integer stopRetryAfterHoursCount,
	                                     Integer turnOffQueuingAfterRetryCount) {
	
		// get current job, generate task
		ScheduledRepeatingJob existingJob = (ScheduledRepeatingJob)jobManager.getJob(existingJobId);
        	
		// delete old job
    	jobManager.deleteJob(existingJob);
    	
    	// schedule new job
    	YukonJob replacementJob = schedule(name, groupName, command, attributes, type, cronExpression, userContext, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
    	log.info("Job replaced. old jobId=" + existingJob.getId() + " deleted, replacement jobId=" + replacementJob.getId() + ", name=" + name + ", groupName=" + groupName + ", attribute=" + attributes + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + 
    	         ", retryCount=" + retryCount + ", stopRetryAfterHoursCount=" + stopRetryAfterHoursCount + ", turnOffQueuingAfterRetryCount=" + turnOffQueuingAfterRetryCount + ".");
    	
		return replacementJob;
	}
	
	
	public void disableJob(int existingJobId) {
		
		YukonJob existingJob = jobManager.getJob(existingJobId);
		jobManager.disableJob(existingJob);
	}
	
	public String getCronExpression(int jobId) {
		
		ScheduledRepeatingJob job = (ScheduledRepeatingJob)jobManager.getJob(jobId);
		return job.getCronString();
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
