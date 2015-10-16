package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.dao.impl.DeviceCommandServiceImpl.CompletionCallback;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
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
    private DeviceRequestType commandRequestExecutionType;
    private int retryCount = 0;
    private Integer stopRetryAfterHoursCount = null;
    private Integer turnOffQueuingAfterRetryCount = null;
    private CompletionCallback completionCallback = null;
    
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao;
    @Autowired private DeviceCommandService commandService;

    @Override
    public void start() {
        startTask();
    }
    
    @Override
    public void stop() {

        int jobId = getJob().getId();
        log.info("Stopping job id=" + jobId);
        long commandsCancelled = completionCallback.cancelExecution();
        log.info("Stopped job id=" + jobId + ". " + commandsCancelled + " commands were cancelled.");
    }

    private void startTask() {
    	LiteYukonUser user = getUserContext().getYukonUser();
    	        
        try {            
        	DeviceGroup taskDeviceGroup = getDeviceGroup();
        	        	
            if (taskDeviceGroup == null) {
                throw new IllegalArgumentException("DeviceGroup does not exist, task cannot run.");
            }
             
            if (log.isDebugEnabled()) {
                log.debug("Job id=" + getJob().getId() + ", Job name=" + getJob().getJobDefinition().getName()
                    + ", Execution type=" + commandRequestExecutionType + ", Device Group=" + taskDeviceGroup.getName()
                    + " started by " + user);
            }
            final CountDownLatch taskCompletionLatch = new CountDownLatch(1);
            CommandCompletionCallbackAdapter<CommandRequestDevice> callback =
                new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
                    @Override
                    public void complete() {
                        taskCompletionLatch.countDown();
                    }
                };
            Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singletonList(getDeviceGroup()));
            completionCallback = commandService.execute(devices, attributes, command, commandRequestExecutionType, user,
                getRetryParameters(), callback);

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

            if (log.isDebugEnabled()) {
                log.debug("Job with id " + getJob().getId() + " Job name=" + getJob().getJobDefinition().getName()
                    + " Context Id=" + completionCallback.getContextId() + " finished.");
            }
        } catch (NotFoundException e) {
            log.error("Could not run command due to missing device group. command = " + getCommand() + ", name="
                + getName() + ", groupName = " + getDeviceGroup().getFullName() + ", user = " + user.getUsername()
                + ", retryCount=" + getRetryCount()
                + ", turnOffQueuingAfterRetryCount=" + getTurnOffQueuingAfterRetryCount() + ".", e);
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
		return commandRequestExecutionType;
	}
	
	public void setCommandRequestExecutionType(DeviceRequestType commandRequestExecutionType) {
		this.commandRequestExecutionType = commandRequestExecutionType;
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
}
