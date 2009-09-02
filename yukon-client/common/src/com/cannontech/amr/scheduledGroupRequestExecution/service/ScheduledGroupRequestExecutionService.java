package com.cannontech.amr.scheduledGroupRequestExecution.service;

import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public interface ScheduledGroupRequestExecutionService {

	public YukonJob schedule(String name, String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext);
	public YukonJob schedule(String name, String groupName, Set<? extends Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext);
		
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, String command, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext);
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, Set<? extends Attribute> attributes, CommandRequestExecutionType type, String cronExpression, YukonUserContext userContext);
	
	public void disableJob(int existingJobId);
	
	public String getCronExpression(int jobId);
}
