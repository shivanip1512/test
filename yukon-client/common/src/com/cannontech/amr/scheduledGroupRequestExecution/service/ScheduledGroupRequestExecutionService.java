package com.cannontech.amr.scheduledGroupRequestExecution.service;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public interface ScheduledGroupRequestExecutionService {

	public YukonJob schedule(String name, String groupName, String command, DeviceRequestType type, String cronExpression, YukonUserContext userContext, RetryStrategy retryStrategy);
	public YukonJob schedule(String name, String groupName, Set<? extends Attribute> attributes, DeviceRequestType type, String cronExpression, YukonUserContext userContext, RetryStrategy retryStrategy);
		
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, String command, DeviceRequestType type, String cronExpression, YukonUserContext userContext, RetryStrategy retryStrategy);
	public YukonJob scheduleReplacement(int existingJobId, String name, String groupName, Set<? extends Attribute> attributes, DeviceRequestType type, String cronExpression, YukonUserContext userContext, RetryStrategy retryStrategy);

    /**
     * Starts the job with the specified Job ID in HttpServletRequest.
     */
    public Map<String, Object> startJob(HttpServletRequest request, String cronExpression);

    /**
     * Toggles Job status. If disabled, make enabled. If enabled,
     * make disabled.
     * 
     */
    public Map<String, Object> toggleJob(HttpServletRequest request) throws ServletException;
}
