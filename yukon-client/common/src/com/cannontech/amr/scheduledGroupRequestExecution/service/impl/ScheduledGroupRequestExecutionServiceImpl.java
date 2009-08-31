package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionJobTaskContainer;
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
	public YukonJob schedule(String name, String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return schedule(name, groupName, command, null, type, cronExpression, userContext);
	}
	
	// SCHEDULE - ATTRIBUTE
	public YukonJob schedule(String name, String groupName, Set<Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return schedule(name, groupName, null, attributes, type, cronExpression, userContext);
	}
	
	private YukonJob schedule(String name, String groupName, String command, Set<Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {

		ScheduledGroupRequestExecutionTask task = scheduledGroupRequestExecutionJobDefinition.createBean();
		task.setName(name);
    	task.setDeviceGroup(deviceGroupService.resolveGroupName(groupName));
    	task.setAttributes(attributes);
    	task.setCommand(command);
    	task.setCommandRequestExecutionType(type);

    	YukonJob job =  jobManager.scheduleJob(scheduledGroupRequestExecutionJobDefinition, task, cronExpression, userContext);
        
    	log.info("Job scheduled. jobId=" + job.getId() + ", groupName=" + groupName + ", attributes=" + attributes + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + ".");
    	
        return job;
	}
	
	// SCHEDULE REPLACEMENT - COMMAND
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, name, groupName, command, null, type, cronExpression, userContext);
	}
	
	// SCHEDULE REPLACEMENT - ATTRIBUTE
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, Set<Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, name, groupName, null, attributes, type, cronExpression, userContext);
	}
	
	private YukonJob scheduleReplacement(int existingJobId, String name, String groupName, String command, Set<Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
	
		// get current job, generate task
		ScheduledRepeatingJob existingJob = (ScheduledRepeatingJob)jobManager.getJob(existingJobId);
		ScheduledGroupRequestExecutionTask existingTask = (ScheduledGroupRequestExecutionTask)jobManager.instantiateTask(existingJob);
		    
		
		ScheduledGroupRequestExecutionJobTaskContainer existing = new ScheduledGroupRequestExecutionJobTaskContainer(existingTask.getDeviceGroup().getFullName(), 
		                                                                                                             existingTask.getName(),
		                                                                                                             existingTask.getCommandRequestExecutionType(),
		                                                                                                             existingJob.getCronString(),
		                                                                                                             existingJob.getUserContext().getYukonUser().getUserID(),
		                                                                                                             existingTask.getCommand(),
		                                                                                                             existingTask.getAttributes());
		
		ScheduledGroupRequestExecutionJobTaskContainer replacement = new ScheduledGroupRequestExecutionJobTaskContainer(groupName, 
		                                                                                                                name,
		                                                                                                                type,
		                                                                                                                cronExpression,
		                                                                                                                userContext.getYukonUser().getUserID(),
		                                                                                                                command,
		                                                                                                                attributes);
		
        // difference between old task/job and replacement?
        if (!existing.equals(replacement)) {
        	
        	jobManager.deleteJob(existingJob);
        	
        	// schedule new job
        	YukonJob replacementJob = schedule(name, groupName, command, attributes, type, cronExpression, userContext);
        	
        	log.info("Job replaced. old jobId=" + existingJob.getId() + " deleted, replacement jobId=" + replacementJob.getId() + ", name=" + name + ", groupName=" + groupName + ", attribute=" + attributes + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + ".");
        	
    		return replacementJob;
    		
    	// no difference
        } else {
        	
        	jobManager.enableJob(existingJob);
        	return existingJob;
        }
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
