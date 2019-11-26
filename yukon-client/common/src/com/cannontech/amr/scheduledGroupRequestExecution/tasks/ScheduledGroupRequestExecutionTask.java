package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.dao.impl.DeviceCommandServiceImpl.CompletionCallback;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonTaskBase;
public class ScheduledGroupRequestExecutionTask extends YukonTaskBase {

	private final Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionTask.class);

    // Injected variables
	private String name;
    private DeviceGroup deviceGroup;
    private Set<? extends Attribute> attributes = null;
    private String command = null;
    private DeviceRequestType deviceRequestType;
    private int retryCount = 0;
    private Integer stopRetryAfterHoursCount = null;
    private Integer turnOffQueuingAfterRetryCount = new Integer(0);
    private CompletionCallback completionCallback = null;
    private Integer dependentJobGroupId;
    private String dependentJobResultCategories;
    
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao;
    @Autowired private DeviceCommandService commandService;
    
    @Autowired private MeteringEventLogService eventLogService;

    @Override
    public void start() {
        startTask();
    }
    
    @Override
    public void stop() {

        int jobId = getJob().getId();
        log.info("Canceling job id=" + jobId);
        long commandsCanceled = completionCallback.cancelExecution(getUserContext().getYukonUser());
        log.info("Canceling job id=" + jobId + ". " + commandsCanceled + " commands were canceled.");
    }

    private void startTask() {
    	LiteYukonUser user = getUserContext().getYukonUser();
    	        
        try {            
        	DeviceGroup taskDeviceGroup = getDeviceGroup();
        	        	
            if (taskDeviceGroup == null) {
                throw new IllegalArgumentException("DeviceGroup does not exist, task cannot run.");
            }
            
            RetryParameters retryParameters = getRetryParameters();
            eventLogService.jobStarted(deviceRequestType.getShortName(), name, deviceGroup.getFullName(),
                retryParameters.getQueuedTries() + retryParameters.getNonQueuedTries(), user, getJob().getId());
            if (log.isDebugEnabled()) {
                log.debug("Staring ScheduledGroupRequestExecutionTask");
                log.debug(this);
                log.debug(getJob());
            }
            final CountDownLatch taskCompletionLatch = new CountDownLatch(1);
            CommandCompletionCallback<CommandRequestDevice> callback =
                new CommandCompletionCallback<CommandRequestDevice>() {
                    @Override
                    public void complete() {
                        taskCompletionLatch.countDown();
                    }
                };
            Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singletonList(getDeviceGroup()));
            completionCallback = commandService.execute(devices, attributes, command, deviceRequestType, user,
                retryParameters, callback, name, getJob().getId());

            // create ScheduledGroupRequestExecutionResult record
            ScheduledGroupRequestExecutionPair pair = new ScheduledGroupRequestExecutionPair();
            pair.setCommandRequestExecutionContextId(new CommandRequestExecutionContextId(completionCallback.getContextId()));
            pair.setJobId(getJob().getId());

            scheduledGroupRequestExecutionResultsDao.insert(pair);
            try {
                taskCompletionLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Task thread was interrupted while waiting for completion.", e);
            }
            eventLogService.jobCompleted(deviceRequestType.getShortName(), name, getJob().getId(),
                completionCallback.getContextId());
            if (log.isDebugEnabled()) {
                log.debug("Job with id " + getJob().getId() + " Job name=" + getJob().getJobDefinition().getName()
                    + " Context Id=" + completionCallback.getContextId() + " finished.");
            }
        } catch (NotFoundException e) {
            log.error("Could not run command due to missing device group. command = " + getCommand(), e);
            log.error(getJob());
            log.error(this);
        }
    }

    private RetryParameters getRetryParameters() {
        return new RetryParameters(getTurnOffQueuingAfterRetryCount(),
            getRetryCount() - getTurnOffQueuingAfterRetryCount(),
            getStopRetryAfterHoursCount() == null ? 0 : getStopRetryAfterHoursCount());
    }

    // Setters for injected parameters
    public String getName() {
		return name;
	}
    
    public String getShortName() {
        return com.cannontech.common.util.StringUtils.elideCenter(name, 60);
    }
    
    public void setName(String name) {
		this.name = name;
	}
    
    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }
    
    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

	public Set<? extends Attribute> getAttributes() {
        return attributes;
    }
	public void setAttributes(Set<? extends Attribute> attributes) {
        this.attributes = attributes;
    }
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public DeviceRequestType getCommandRequestExecutionType() {
		return deviceRequestType;
	}
	
	public void setCommandRequestExecutionType(DeviceRequestType commandRequestExecutionType) {
		this.deviceRequestType = commandRequestExecutionType;
	}

	public int getRetryCount() {
        return retryCount;
    }
	
	public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
	
	public Integer getStopRetryAfterHoursCount() {
        return stopRetryAfterHoursCount;
    }
    
    public void setStopRetryAfterHoursCount(Integer stopRetryAfterHoursCount) {
        this.stopRetryAfterHoursCount = stopRetryAfterHoursCount;
    }
    
	public Integer getTurnOffQueuingAfterRetryCount() {
		return turnOffQueuingAfterRetryCount;
	}
	
	public void setTurnOffQueuingAfterRetryCount(Integer turnOffQueuingAfterRetryCount) {
		this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
	}

    public Integer getDependentJobGroupId() {
        return dependentJobGroupId;
    }

    public void setDependentJobGroupId(Integer dependentJobGroupId) {
        this.dependentJobGroupId = dependentJobGroupId;
    }

    public String getDependentJobResultCategories() {
        return dependentJobResultCategories;
    }

    public void setDependentJobResultCategories(String dependentJobResultCategories) {
        this.dependentJobResultCategories = dependentJobResultCategories;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        if (getJob() != null) {
            builder.append("JobId", getJob().getId());
        }
        builder.append("Schedule name", name);
        builder.append("Device Request Type", deviceRequestType);
        if(!StringUtils.isEmpty(command)){
            builder.append("Command", command);
        }
        if (attributes != null && !attributes.isEmpty()) {
            builder.append("Attributes", attributes);
        }
        builder.append("Retry Count", retryCount);
        RetryParameters retries = getRetryParameters();
        builder.append("Queued Retries", retries.getQueuedTries());
        builder.append("NonQueued Retries", retries.getNonQueuedTries());
        builder.append("Device Request Type", deviceRequestType);
        if (dependentJobGroupId != null &&  dependentJobGroupId > 0) {
            builder.append("Dependent Job Group Id", dependentJobGroupId);
        }

        return builder.toString();
    }
}
