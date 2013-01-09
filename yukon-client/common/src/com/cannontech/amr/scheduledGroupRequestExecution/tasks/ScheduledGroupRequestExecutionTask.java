package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonTaskBase;
public class ScheduledGroupRequestExecutionTask extends YukonTaskBase {

	private Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionTask.class);

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
    private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao;
    private PlcDeviceAttributeReadService plcDeviceAttributeReadService;

    @Override
    public void start() {
        startTask(getJobContext().getJob().getId());
    }
    
    @Override
    public void stop() {
    	int jobId = getJobContext().getJob().getId();
    	
    	log.info("Stopping job with id " + jobId);
    	long commandsCancelled = currentExecutor.cancelExecution(currentCallback, getYukonUser());
    	log.info("Stopped job with id " + jobId + ". " + commandsCancelled + " commands were cancelled.");
    }
    
    private void startTask(int jobId){
    	
    	LiteYukonUser user = getYukonUser();
    	
    	log.info("Starting scheduled group command execution. user = " + user.getUsername() + ".");
        
        try {
        	DeviceGroup deviceGroup = getDeviceGroup();
            if (deviceGroup == null) {
                throw new IllegalArgumentException("DeviceGroup does not exist, task cannot run.");
            }
            
            final CountDownLatch taskCompletionLatch = new CountDownLatch(1);
            CommandCompletionCallbackAdapter<CommandRequestDevice> dummyCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
        		@Override
        		public void complete() {
        			taskCompletionLatch.countDown();
        		}
        	};
            
        	CommandRequestExecutionObjects<CommandRequestDevice> executionObjects = null;
        	
            if (getCommand() != null) {
            	Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singletonList(getDeviceGroup()));
                List<CommandRequestDevice> commandRequests = new ArrayList<CommandRequestDevice>();
                for (SimpleDevice device : devices) {
                    
                    CommandRequestDevice cmdReq = new CommandRequestDevice();
                    cmdReq.setCommandCallback(new CommandCallbackBase(getCommand()));
                    cmdReq.setDevice(device);
                    
                    commandRequests.add(cmdReq);
                }

                CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor = 
                    new CommandRequestRetryExecutor<CommandRequestDevice>(commandRequestDeviceExecutor, 
                            getRetryParameters());
                executionObjects = retryExecutor.execute(commandRequests, dummyCallback, getCommandRequestExecutionType(), user);
                currentExecutor = commandRequestDeviceExecutor;
                currentCallback = executionObjects.getCallback();

            } else if (getAttributes() != null) {
            	DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(getDeviceGroup());
    	        
    	        executionObjects = plcDeviceAttributeReadService.backgroundReadDeviceCollection(deviceCollection, 
    	                                                                                 attributes, 
    	                                                                                 getCommandRequestExecutionType(), 
    	                                                                                 dummyCallback, 
    	                                                                                 user, 
    	                                                                                 getRetryParameters());
    	        
    	        currentExecutor = executionObjects.getCommandRequestExecutor();
    	        currentCallback = executionObjects.getCallback();
    	        
            } else {
            	throw new UnsupportedOperationException("A command string or attribute is required to run task.");
            }
            
	        // create ScheduledGroupRequestExecutionResult record
	        ScheduledGroupRequestExecutionPair pair = new ScheduledGroupRequestExecutionPair();
	        pair.setCommandRequestExecutionContextId(executionObjects.getContextId());
	        pair.setJobId(getJobContext().getJob().getId());
	        
	        scheduledGroupRequestExecutionResultsDao.insert(pair);
	        
	        //Causes the method to block until the task completes
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
	
    // Setters for injected services and daos
	@Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
		this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
	}
	
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
    
    @Autowired
    public void setScheduledGroupRequestExecutionResultsDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao) {
		this.scheduledGroupRequestExecutionResultsDao = scheduledGroupRequestExecutionResultsDao;
	}
    @Autowired
    public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
        this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
    }
}
