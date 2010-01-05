package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ResultResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Implementation class for GroupCommandExecutor
 */
public class GroupCommandExecutorImpl implements GroupCommandExecutor {

    private CommandRequestDeviceExecutor commandRequestExecutor;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    private RecentResultsCache<GroupCommandResult> resultsCache
        = new RecentResultsCache<GroupCommandResult>();
    
    private Logger log = YukonLogManager.getLogger(GroupCommandExecutorImpl.class);
    
    @Autowired
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    public String execute(DeviceCollection deviceCollection, final String command, CommandRequestExecutionType commandRequestExecutionType, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        
        if (commandRequestExecutionType == null) {
            throw new IllegalArgumentException("commandRequestExecutionType cannot be null");
        }
        
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                return buildStandardRequest(from, command);
            }
        };
        
    	List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(deviceCollection.getDeviceList(), objectMapper);
    	
    	return execute(deviceCollection, command, requests, commandRequestExecutionType, callback, user);
    }
    
    public String execute(final DeviceCollection deviceCollection, final String command, List<CommandRequestDevice> requests, CommandRequestExecutionType commandRequestExecutionType, final SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        
        if (commandRequestExecutionType == null) {
            throw new IllegalArgumentException("commandRequestExecutionType cannot be null");
        }

        // create temporary groups
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        final StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        final GroupCommandResult groupCommandResult = new GroupCommandResult();
        
        GroupCommandCompletionCallback commandCompletionCallback = new GroupCommandCompletionCallback() {
            @Override
            public void doComplete() {
                try {
                    callback.handle(groupCommandResult);
                } catch (Exception e) {
                    log.warn("There was an error executing the callback", e);
                }
            }
            
            @Override
            public void handleSuccess(SimpleDevice device) {
                deviceGroupMemberEditorDao.addDevices(successGroup, device);
            }
            
            @Override
            public void handleFailure(SimpleDevice device) {
                deviceGroupMemberEditorDao.addDevices(failureGroup, device);
            }
            
        };
        
        groupCommandResult.setCommandRequestExecutionType(commandRequestExecutionType);
        groupCommandResult.setCommand(command);
        groupCommandResult.setResultHolder(commandCompletionCallback);
        groupCommandResult.setDeviceCollection(deviceCollection);
        groupCommandResult.setCallback(commandCompletionCallback);
        
        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        groupCommandResult.setSuccessCollection(successCollection);
        DeviceCollection failureCollectioin = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        groupCommandResult.setFailureCollection(failureCollectioin);
        groupCommandResult.setStartTime(new Date());
        
        String key = resultsCache.addResult(groupCommandResult);
        groupCommandResult.setKey(key);
        
        CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = commandRequestExecutor.execute(requests, commandCompletionCallback, commandRequestExecutionType, user);
        groupCommandResult.setCommandRequestExecutionIdentifier(commandRequestExecutionIdentifier);
        
        log.debug("executing " + command + " on the " + requests.size() + " devices in " + deviceCollection);
        
        return key;
    }
    
    @Override
    public long cancelExecution(String resultId, LiteYukonUser user) throws ResultResultExpiredException {

        GroupCommandResult result = getResult(resultId);
        long commandsCanceled = commandRequestExecutor.cancelExecution(result.getCallback(), user);
        
        return commandsCanceled;
    }
    
    private CommandRequestDevice buildStandardRequest(YukonDevice device, final String command) {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setDevice(new SimpleDevice(device.getPaoIdentifier()));
        
        final String commandStr = command + " update";
        request.setCommand(commandStr);
        return request;
    }

    public List<GroupCommandResult> getCompleted() {
        return resultsCache.getCompleted();
    }
    
    public List<GroupCommandResult> getCompletedByType(CommandRequestExecutionType type) {
    	List<GroupCommandResult> completedOfType = new ArrayList<GroupCommandResult>();
    	List<GroupCommandResult> completed = getCompleted();
    	for (GroupCommandResult result : completed) {
    		if (result.getCommandRequestExecutionType().equals(type)) {
    			completedOfType.add(result);
    		}
    	}
    	return completedOfType;
    }

    public List<GroupCommandResult> getPending() {
        return resultsCache.getPending();
    }
    
    public List<GroupCommandResult> getPendingByType(CommandRequestExecutionType type) {
    	List<GroupCommandResult> pendingOfType = new ArrayList<GroupCommandResult>();
    	List<GroupCommandResult> pending = getPending();
    	for (GroupCommandResult result : pending) {
    		if (result.getCommandRequestExecutionType().equals(type)) {
    			pendingOfType.add(result);
    		}
    	}
    	return pendingOfType;
    }

    public GroupCommandResult getResult(String id) throws ResultResultExpiredException {
    	
    	GroupCommandResult result = resultsCache.getResult(id);
    	
    	// friendly exception
        if (result == null) {
			throw new ResultResultExpiredException("Group Command Result No Longer Exists");
		}
        
        return result;
    }
}
