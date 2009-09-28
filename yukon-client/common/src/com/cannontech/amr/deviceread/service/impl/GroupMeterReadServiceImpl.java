package com.cannontech.amr.deviceread.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionContext;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCollections;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

public class GroupMeterReadServiceImpl implements GroupMeterReadService {

	private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
	private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
	private TemporaryDeviceGroupService temporaryDeviceGroupService;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	
	private Logger log = YukonLogManager.getLogger(GroupMeterReadServiceImpl.class);
	private RecentResultsCache<GroupMeterReadResult> resultsCache = new RecentResultsCache<GroupMeterReadResult>();
	
	public CommandRequestExecutionContext readDeviceCollectionWithRetry(DeviceCollection deviceCollection, 
                                                                   Set<? extends Attribute> attributes, 
                                                                   CommandRequestExecutionType type, 
                                                                   CommandCompletionCallback<CommandRequestDevice> callback, 
                                                                   LiteYukonUser user,
                                                                   int retryCount,
                                                                   Date stopRetryAfterDate,
                                                                   Integer turnOffQueuingAfterRetryCount) {
	    
	    
	    Multimap<YukonDevice, CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(deviceCollection.getDeviceList(), attributes, type);
	    List<CommandRequestDevice> allRequests = new ArrayList<CommandRequestDevice>(commandRequests.values());
	    
	    CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor = new CommandRequestRetryExecutor<CommandRequestDevice>(commandRequestDeviceExecutor, retryCount, stopRetryAfterDate, turnOffQueuingAfterRetryCount);
        CommandRequestExecutionContext context = retryExecutor.execute(allRequests, callback, type, user);
        
        return context;
	}
	
	
	
	public String readDeviceCollection(DeviceCollection deviceCollection, 
										Set<? extends Attribute> attributes, 
										CommandRequestExecutionType type, 
										final SimpleCallback<GroupMeterReadResult> callback, 
										LiteYukonUser user) {
    	
        Multimap<YukonDevice, CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(deviceCollection.getDeviceList(), attributes, type);
        
        // unsupported
        List<PaoIdentifier> unsupportedDevices = new ArrayList<PaoIdentifier>();
        Set<YukonDevice> allDeviceSet = new HashSet<YukonDevice>(deviceCollection.getDeviceList());
        Set<YukonDevice> supportedDeviceSet = commandRequests.keySet();
        allDeviceSet.removeAll(supportedDeviceSet);
        for (YukonDevice device : allDeviceSet) {
            log.debug("It isn't possible to read " + attributes + " for  " + device);
            unsupportedDevices.add(device.getPaoIdentifier());
        }
        
        List<CommandRequestDevice> allRequests = new ArrayList<CommandRequestDevice>(commandRequests.values());
    	
        // result 
        final GroupMeterReadResult groupMeterReadResult = new GroupMeterReadResult();
        
        // success/fail temporary groups
        final StoredDeviceGroup originalDeviceCollectionCopyGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(originalDeviceCollectionCopyGroup, deviceCollection.getDeviceList());
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        final StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        final StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, PaoCollections.asDeviceList(unsupportedDevices));
        
        // command completion callback
        GroupCommandCompletionCallback commandCompletionCallback = new GroupCommandCompletionCallback() {
            
        	@Override
            public void doComplete() {
                try {
                    callback.handle(groupMeterReadResult);
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
        
        groupMeterReadResult.setAttributes(attributes);
        groupMeterReadResult.setCommandRequestExecutionType(type);
        groupMeterReadResult.setResultHolder(commandCompletionCallback);
        groupMeterReadResult.setDeviceCollection(deviceCollection);
        groupMeterReadResult.setCallback(commandCompletionCallback);
        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        groupMeterReadResult.setSuccessCollection(successCollection);
        DeviceCollection failureCollectioin = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        groupMeterReadResult.setFailureCollection(failureCollectioin);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        groupMeterReadResult.setUnsupportedCollection(unsupportedCollection);
        DeviceCollection originalDeviceCollectionCopy = deviceGroupCollectionHelper.buildDeviceCollection(originalDeviceCollectionCopyGroup);
        groupMeterReadResult.setOriginalDeviceCollectionCopy(originalDeviceCollectionCopy);
        groupMeterReadResult.setStartTime(new Date());
        
    	// execute
    	CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = commandRequestDeviceExecutor.execute(allRequests, commandCompletionCallback, type, user);
    	groupMeterReadResult.setCommandRequestExecutionIdentifier(commandRequestExecutionIdentifier);
    	
    	// add to cache
    	String key = resultsCache.addResult(groupMeterReadResult);
        groupMeterReadResult.setKey(key);
    	
    	
    	return key;
    }
	
	@Override
	public List<GroupMeterReadResult> getCompleted() {
        return resultsCache.getCompleted();
    }
	
	@Override
	public List<GroupMeterReadResult> getCompletedByType(CommandRequestExecutionType type) {
        List<GroupMeterReadResult> completed = getCompleted();
        List<GroupMeterReadResult> completeOfType = new ArrayList<GroupMeterReadResult>();
        for (GroupMeterReadResult result : completed) {
        	if (result.getCommandRequestExecutionType().equals(type)) {
        		completeOfType.add(result);
        	}
        }
        return completeOfType;
    }

	@Override
	public List<GroupMeterReadResult> getPending() {
        return resultsCache.getPending();
    }
	
	@Override
    public List<GroupMeterReadResult> getPendingByType(CommandRequestExecutionType type) {
        List<GroupMeterReadResult> pending = getPending();
        List<GroupMeterReadResult> pendingOfType = new ArrayList<GroupMeterReadResult>();
        for (GroupMeterReadResult result : pending) {
        	if (result.getCommandRequestExecutionType().equals(type)) {
        		pendingOfType.add(result);
        	}
        }
        return pendingOfType;
    }

	@Override
    public GroupMeterReadResult getResult(String id) {
        return resultsCache.getResult(id);
    }
	
	
	
	@Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
		this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
	}

	@Autowired
	public void setMeterReadCommandGeneratorService(MeterReadCommandGeneratorService meterReadCommandGeneratorService) {
		this.meterReadCommandGeneratorService = meterReadCommandGeneratorService;
	}
	
	@Autowired
	public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
		this.temporaryDeviceGroupService = temporaryDeviceGroupService;
	}
	
	@Autowired
	public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
	
	@Autowired
	public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
}
