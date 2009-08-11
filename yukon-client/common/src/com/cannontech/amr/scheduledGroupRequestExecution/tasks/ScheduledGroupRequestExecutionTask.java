package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledGroupRequestExecutionTask extends YukonTaskBase {

	private Logger log = YukonLogManager.getLogger(ScheduledGroupRequestExecutionTask.class);

    // Injected variables
	private String name;
    private String groupName;
    private Attribute attribute = null;
    private String command = null;
    private CommandRequestExecutionType commandRequestExecutionType;

    // Injected services and daos
    private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    private GroupMeterReadService groupMeterReadService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionResultsDao;

    public void start(int jobId) {
        startTask(jobId);
    }
    
    private void startTask(int jobId){
    	
    	LiteYukonUser user = getUserContext().getYukonUser();
    	
    	log.info("Starting scheduled group command execution. user = " + user.getUsername() + ".");
        
        try {
        	
	        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(getGroupName());
	        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(deviceGroup);
	        
	        List<CommandRequestDevice> commandRequests = new ArrayList<CommandRequestDevice>();
	        for (SimpleDevice device : deviceCollection.getDeviceList()) {
	        	
	        	CommandRequestDevice cmdReq = new CommandRequestDevice();
	            cmdReq.setCommand(getCommand());
	            cmdReq.setDevice(device);
	            
	            commandRequests.add(cmdReq);
	        }
            
	        
            CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = null;
            if (getCommand() != null) {
            
            	CommandCompletionCallbackAdapter<CommandRequestDevice> dummyCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
                };
                
            	commandRequestExecutionIdentifier = commandRequestDeviceExecutor.execute(commandRequests, dummyCallback, getCommandRequestExecutionType(), user);
            
            } else if (getAttribute() != null) {
            	
            	SimpleCallback<GroupMeterReadResult> dummyCallback = new SimpleCallback<GroupMeterReadResult>() {
            		@Override
            		public void handle(GroupMeterReadResult item) throws Exception {
            			
            		}
                };
                
            	Set<Attribute> attributeSet = new HashSet<Attribute>();
    	        attributeSet.add(getAttribute());
            	String resultKey = groupMeterReadService.readDeviceCollection(deviceCollection, attributeSet, getCommandRequestExecutionType(), dummyCallback, user);
            	GroupMeterReadResult groupMeterReadResult = groupMeterReadService.getResult(resultKey);
            	commandRequestExecutionIdentifier = groupMeterReadResult.getCommandRequestExecutionIdentifier();
            
            } else {
            	throw new UnsupportedOperationException("A command string or attribute is required to run task.");
            }
            
	        // create ScheduledGroupRequestExecutionResult record
	        ScheduledGroupRequestExecutionPair pair = new ScheduledGroupRequestExecutionPair();
	        pair.setCommandRequestExecutionId(commandRequestExecutionIdentifier.getCommandRequestExecutionId());
	        pair.setJobId(jobId);
	        
	        scheduledGroupRequestExecutionResultsDao.insert(pair);
	        
        } catch (NotFoundException e) {
        	log.error("Could not run command due to missing device group. command = " + getCommand() + ", name=" + getName() + ", groupName = " + getGroupName() + ", user = " + user.getUsername() + ".", e);
        } catch (PaoAuthorizationException e) {
        	log.error("Could not run command due to authorization exception. command = " + getCommand() + ", name=" + getName() + ", groupName = " + getGroupName() + ", user = " + user.getUsername() + ".", e);
        }
        
    }

    public void stop(int jobId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Setters for injected parameters
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
    
    public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Attribute getAttribute() {
		return attribute;
	}
	
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	public CommandRequestExecutionType getCommandRequestExecutionType() {
		return commandRequestExecutionType;
	}
	
	public void setCommandRequestExecutionType(CommandRequestExecutionType commandRequestExecutionType) {
		this.commandRequestExecutionType = commandRequestExecutionType;
	}

    // Setters for injected services and daos
	@Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
		this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
	}
	
	@Autowired
	public void setGroupMeterReadService(GroupMeterReadService groupMeterReadService) {
		this.groupMeterReadService = groupMeterReadService;
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
}
