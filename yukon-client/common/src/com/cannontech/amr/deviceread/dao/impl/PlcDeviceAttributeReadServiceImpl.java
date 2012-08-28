package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PlcDeviceAttributeReadServiceImpl implements PlcDeviceAttributeReadService {
    private static final Logger log = YukonLogManager.getLogger(PlcDeviceAttributeReadServiceImpl.class);
    private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    private AttributeService attributeService;
    private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private RecentResultsCache<GroupMeterReadResult> resultsCache = new RecentResultsCache<GroupMeterReadResult>();
    
    public boolean isReadable(YukonPao device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.debug("Validating Readability for" + attributes + " on device " + device + " for " + user);
        
        List<PaoMultiPointIdentifier> paoPointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(ImmutableSet.of(device), attributes);
        
        return isReadable(paoPointIdentifiers, user);
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }
    
    @Override
    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes, DeviceRequestType type, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        List<PaoMultiPointIdentifier> paoPointIdentifiers = attributeService.findPaoMultiPointIdentifiersForAttributes(ImmutableSet.of(device), attributes);
        
        List<CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(paoPointIdentifiers);
        if (commandRequests.isEmpty()) {
            throw new RuntimeException("It isn't possible to read " + attributes + " for  " + device);
        }
        
        CommandRequestExecutionTemplate<CommandRequestDevice> executionTemplate = commandRequestDeviceExecutor.getExecutionTemplate(type, user);
        CollectingCommandCompletionCallback callback = new CollectingCommandCompletionCallback();
        WaitableCommandCompletionCallback<Object> waitableCallback = waitableCommandCompletionCallbackFactory.createWaitable(callback);
        executionTemplate.execute(commandRequests, waitableCallback);
        try {
            waitableCallback.waitForCompletion();
            return callback;
        } catch (InterruptedException e) {
            throw new MeterReadRequestException(e);
        } catch (TimeoutException e) {
            throw new MeterReadRequestException(e);
        }
    }
    
    @Override
    public CommandRequestExecutionContextId backgroundReadDeviceCollection(final Iterable<PaoMultiPointIdentifier> pointsToRead, 
                                                                           DeviceRequestType type, 
                                                                           CommandCompletionCallback<CommandRequestDevice> callback, 
                                                                           LiteYukonUser user,
                                                                           RetryParameters retryParameters) {


        List<CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(pointsToRead);

        CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor = new CommandRequestRetryExecutor<CommandRequestDevice>(commandRequestDeviceExecutor, retryParameters);

        CommandRequestExecutionContextId contextId = retryExecutor.execute(commandRequests, callback, type, user);

        return contextId;
    }

    @Override
    public CommandRequestExecutionContextId backgroundReadDeviceCollection(DeviceCollection deviceCollection,
                                                                           Set<? extends Attribute> attributes,
                                                                           DeviceRequestType type,
                                                                           CommandCompletionCallback<CommandRequestDevice> callback,
                                                                           LiteYukonUser user,
                                                                           RetryParameters retryParameters) {
        List<PaoMultiPointIdentifier> attributePointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);
        return backgroundReadDeviceCollection(attributePointIdentifiers, type, callback, user, retryParameters);
    }

    @Override
    public String readDeviceCollection(DeviceCollection deviceCollection, 
                                        Set<? extends Attribute> attributes, 
                                        DeviceRequestType type, 
                                        final SimpleCallback<GroupMeterReadResult> callback, 
                                        LiteYukonUser user) {
        Set<PaoIdentifier> unsupportedDevices = Sets.newHashSet(PaoUtils.asPaoIdentifiers(deviceCollection));
        List<PaoMultiPointIdentifier> paoPointIdentifiers = 
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);
        List<CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(paoPointIdentifiers);
        for (CommandRequestDevice commandRequestDevice : commandRequests) {
            unsupportedDevices.remove(commandRequestDevice.getDevice().getPaoIdentifier());
        }
        
        for (PaoIdentifier device : unsupportedDevices) {
            log.debug("It isn't possible to read " + attributes + " for  " + device);
        }
        
        // result 
        final GroupMeterReadResult groupMeterReadResult = new GroupMeterReadResult();
        
        // success/fail temporary groups
        final StoredDeviceGroup originalDeviceCollectionCopyGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(originalDeviceCollectionCopyGroup, deviceCollection.getDeviceList());
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, PaoUtils.asDeviceList(unsupportedDevices));
        
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
        CommandRequestExecutionTemplate<CommandRequestDevice> executionTemplate = commandRequestDeviceExecutor.getExecutionTemplate(type, user);
        CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = executionTemplate.execute(commandRequests, commandCompletionCallback);
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
    public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type) {
        List<GroupMeterReadResult> completed = getCompleted();
        return filterByType(completed, type);
    }

    @Override
    public List<GroupMeterReadResult> getPending() {
        return resultsCache.getPending();
    }
    
    @Override
    public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type) {
        List<GroupMeterReadResult> pending = getPending();
        return filterByType(pending, type);
    }



    private List<GroupMeterReadResult> filterByType(List<GroupMeterReadResult> pending,
                                                    DeviceRequestType type) {
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
    public void setMeterReadCommandGeneratorService(MeterReadCommandGeneratorService meterReadCommandGeneratorService) {
		this.meterReadCommandGeneratorService = meterReadCommandGeneratorService;
	}
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setCommandRequestDeviceExecutor(CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
        this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
    }
    
    @Autowired
    public void setWaitableCommandCompletionCallbackFactory(WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory) {
        this.waitableCommandCompletionCallbackFactory = waitableCommandCompletionCallbackFactory;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
}
