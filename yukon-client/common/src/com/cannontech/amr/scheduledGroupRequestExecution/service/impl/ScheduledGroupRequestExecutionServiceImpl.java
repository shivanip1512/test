package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;

public class ScheduledGroupRequestExecutionServiceImpl implements ScheduledGroupRequestExecutionService {

	private JobManager jobManager;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
    
    private Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionServiceImpl.class);
    
    // SCHEDULE - COMMAND
	public YukonJob schedule(String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return schedule(groupName, command, null, type, cronExpression, userContext);
	}
	
	// SCHEDULE - ATTRIBUTE
	public YukonJob schedule(String groupName, Attribute attribute, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return schedule(groupName, null, attribute, type, cronExpression, userContext);
	}
	
	private YukonJob schedule(String groupName, String command, Attribute attribute, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {

		ScheduledGroupRequestExecutionTask task = scheduledGroupRequestExecutionJobDefinition.createBean();
    	task.setGroupName(groupName);
    	task.setAttribute(attribute);
    	task.setCommand(command);
    	task.setCommandRequestExecutionType(type);

    	YukonJob job =  jobManager.scheduleJob(scheduledGroupRequestExecutionJobDefinition, task, cronExpression, userContext);
        
    	log.info("Job scheduled. jobId=" + job.getId() + ", groupName=" + groupName + ", attribute=" + attribute + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + ".");
    	
        return job;
	}
	
	// SCHEDULE REPLACEMENT - COMMAND
	public YukonJob scheduleReplacement(int existingJobId, String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, groupName, command, null, type, cronExpression, userContext);
	}
	
	// SCHEDULE REPLACEMENT - ATTRIBUTE
	public YukonJob scheduleReplacement(int existingJobId, String groupName, Attribute attribute, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
		
		return scheduleReplacement(existingJobId, groupName, null, attribute, type, cronExpression, userContext);
	}
	
	private YukonJob scheduleReplacement(int existingJobId, String groupName, String command, Attribute attribute, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext) {
	
		// get current job, generate task
		ScheduledRepeatingJob existingJob = (ScheduledRepeatingJob)jobManager.getJob(existingJobId);
		ScheduledGroupRequestExecutionTask existingTask = new ScheduledGroupRequestExecutionTask();
		InputRoot inputRoot = scheduledGroupRequestExecutionJobDefinition.getInputs();
        InputUtil.applyProperties(inputRoot, existingTask, existingJob.getJobProperties());
		
        if (attribute != null) {
        	if (existingTask.getAttribute() != null) {
        		
        	}
        }
        
        // difference between old task/job and replacement?
        if (!existingTask.getGroupName().equals(groupName)
        	|| (attribute != null && existingTask.getAttribute() != null && !attribute.equals(existingTask.getAttribute())) // new attribute
        	|| (command != null && existingTask.getCommand() != null && !command.equals(existingTask.getCommand())) // new command
        	|| (attribute != null && existingTask.getCommand() != null) // switching from command to attribute
        	|| (command != null &&existingTask.getAttribute() != null) // switching from attribute to command
        	|| !existingTask.getCommandRequestExecutionType().equals(type)
        	|| !existingJob.getCronString().equals(cronExpression)
        	|| (existingJob.getUserContext().getYukonUser().getUserID() != userContext.getYukonUser().getUserID())) {
        	
        	jobManager.deleteJob(existingJob);
        	
        	// schedule new job
        	YukonJob replacementJob = schedule(groupName, command, attribute, type, cronExpression, userContext);
        	
        	log.info("Job replaced. old jobId=" + existingJob.getId() + " deleted, replacement jobId=" + replacementJob.getId() + ", groupName=" + groupName + ", attribute=" + attribute + ", command=" + command + ", cronExpression=" + cronExpression + ", user=" + userContext.getYukonUser().getUsername() + ".");
        	
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
}
