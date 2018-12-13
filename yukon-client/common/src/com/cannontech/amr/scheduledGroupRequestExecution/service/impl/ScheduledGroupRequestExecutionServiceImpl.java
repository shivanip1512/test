package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;

public class ScheduledGroupRequestExecutionServiceImpl implements ScheduledGroupRequestExecutionService {

	private JobManager jobManager;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> jobDefinition;
    @Autowired private DeviceGroupService deviceGroupService;
    private Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionServiceImpl.class);
    
    @Override
    public YukonJob schedule(String name, String groupName, String command, DeviceRequestType type,
            String cronExpression, YukonUserContext userContext, RetryStrategy retryStrategy) {

        log.info("Scheduling job. name=" + name + " groupName=" + groupName);     
        ScheduledGroupRequestExecutionTask task = buildTask(name, groupName, command, null, type, retryStrategy);
        YukonJob job = jobManager.scheduleJob(jobDefinition, task, cronExpression, userContext);
        log.info("Task=" + task);
        log.info("Job scheduled=" + job);

        return job;
    }

    @Override
    public YukonJob schedule(String name, String groupName, Set<? extends Attribute> attributes, DeviceRequestType type,
            String cronExpression, YukonUserContext context, RetryStrategy retryStrategy) {

        log.info("Scheduling job. name=" + name + " groupName=" + groupName);    
        ScheduledGroupRequestExecutionTask task = buildTask(name, groupName, null, attributes, type, retryStrategy);
        YukonJob job = jobManager.scheduleJob(jobDefinition, task, cronExpression, context);
        log.info("Task=" + task);
        log.info("Job scheduled=" + job);

        return job;
    }

    @Override
    public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, String command,
            DeviceRequestType type, String cronExpression, YukonUserContext context, RetryStrategy retryStrategy) {

        log.info("Replacing job. existingJobId=" + existingJobId + " name=" + name + " groupName=" + groupName);
        ScheduledGroupRequestExecutionTask task = buildTask(name, groupName, command, null, type, retryStrategy);
        YukonJob job = jobManager.replaceScheduledJob(existingJobId, jobDefinition, task, cronExpression, context);
        log.info("Task=" + task);
        log.info("Job replaced=" + job);

        return job;
    }

    @Override
    public YukonJob scheduleReplacement(int existingJobId, String name, String groupName,
            Set<? extends Attribute> attributes, DeviceRequestType type, String cronExpression,
            YukonUserContext context, RetryStrategy retryStrategy) {

        log.info("Replacing job. existingJobId=" + existingJobId + " name=" + name + " groupName=" + groupName);
        ScheduledGroupRequestExecutionTask task = buildTask(name, groupName, null, attributes, type, retryStrategy);
        YukonJob job =
            jobManager.replaceScheduledJob(existingJobId, jobDefinition, task, cronExpression, context);
        log.info("Task=" + task);
        log.info("Job replaced=" + job);

        return job;
    }
	
    private ScheduledGroupRequestExecutionTask buildTask(String name, String groupName, String command,
            Set<? extends Attribute> attributes, DeviceRequestType type, RetryStrategy retryStrategy) {

        int retryCount = retryStrategy.getRetryCount();
        Integer stopRetryAfterHoursCount = retryStrategy.getStopRetryAfterHoursCount();
        Integer turnOffQueuingAfterRetryCount = retryStrategy.getTurnOffQueuingAfterRetryCount();

        ScheduledGroupRequestExecutionTask task = jobDefinition.createBean();
        task.setName(name);
        task.setDeviceGroup(deviceGroupService.resolveGroupName(groupName));
        task.setAttributes(attributes);
        task.setCommand(command);
        task.setCommandRequestExecutionType(type);
        task.setRetryCount(retryCount);
        task.setStopRetryAfterHoursCount(stopRetryAfterHoursCount);
        task.setTurnOffQueuingAfterRetryCount(turnOffQueuingAfterRetryCount);
        return task;
    }
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Resource(name="scheduledGroupRequestExecutionJobDefinition")
	public void setScheduledGroupRequestExecutionjobDefinition(
			YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
		this.jobDefinition = scheduledGroupRequestExecutionJobDefinition;
	}
}
