package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceCommandService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutor;
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
    private CommandRequestExecutor<CommandRequestDevice> currentExecutor = null;
    private CommandCompletionCallback<? super CommandRequestDevice> currentCallback = null;
    
    // Injected services and daos
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao;
    @Autowired private DeviceCommandService devicePorterCommandService;

    @Override
    public void start() {
        startTask();
    }
    
    @Override
    public void stop() {
    	int jobId = getJob().getId();
    	
    	log.info("Stopping job with id " + jobId);
    	long commandsCancelled = currentExecutor.cancelExecution(currentCallback, getUserContext().getYukonUser(), true);
    	log.info("Stopped job with id " + jobId + ". " + commandsCancelled + " commands were cancelled.");
    }
    
    private void startTask() {
    	LiteYukonUser user = getUserContext().getYukonUser();
    	
    	log.info("Starting scheduled group command execution. user = " + user.getUsername() + ".");
        try {
        	DeviceGroup taskDeviceGroup = getDeviceGroup();
            if (taskDeviceGroup == null) {
                throw new IllegalArgumentException("DeviceGroup does not exist, task cannot run.");
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
            CommandRequestExecutionObjects<CommandRequestDevice> executionObjects =
                devicePorterCommandService.execute(devices, attributes, command, commandRequestExecutionType, user,
                    getRetryParameters(), callback);
            currentExecutor = executionObjects.getCommandRequestExecutor();
            currentCallback = executionObjects.getCallback();

            // create ScheduledGroupRequestExecutionResult record
            ScheduledGroupRequestExecutionPair pair = new ScheduledGroupRequestExecutionPair();
            pair.setCommandRequestExecutionContextId(executionObjects.getContextId());
            pair.setJobId(getJob().getId());

            scheduledGroupRequestExecutionResultsDao.insert(pair);
            try {
                taskCompletionLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException("Task thread was interrupted while waiting for completion.", e);
            }
            
        } catch (NotFoundException e) {
        	log.error("Could not run command due to missing device group. command = " + getCommand() + ", name=" + getName() + ", groupName = " + getDeviceGroup().getFullName() + ", user = " + user.getUsername() + 
        	          ", retryCount=" + getRetryCount() + ", stopRetryAfterDate=" + getStopRetryAfterDate() + ", turnOffQueuingAfterRetryCount=" + getTurnOffQueuingAfterRetryCount() + ".", e);
        }
        
    }

    private RetryParameters getRetryParameters() {
        return new RetryParameters(getRetryCount(), getStopRetryAfterDate(), getTurnOffQueuingAfterRetryCount());
    }
    
    private Date getStopRetryAfterDate() {
        
        Date stopRetryAfterDate = null;
        Integer hours = getStopRetryAfterHoursCount();
        if (hours != null) {
            stopRetryAfterDate = new Date();
            stopRetryAfterDate = DateUtils.addHours(stopRetryAfterDate, hours);
        }
        
        return stopRetryAfterDate;
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
