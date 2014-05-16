package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {

    private static final LogHelper log = LogHelper.getInstance(YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class));

    @Autowired private AttributeService attributeService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private NextValueHelper nextValueHelper;
    /*plcDeviceAttributeReadService should be deleted by YUK-12715 and replaced by DeviceAttributeReadService*/
    @Autowired private PlcDeviceAttributeReadService plcDeviceAttributeReadService;

    private ImmutableMap<StrategyType, DeviceAttributeReadStrategy> strategies = ImmutableMap.of();
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes,
                              LiteYukonUser user) {

        log.debug("isReadable called for %.3s and %s", devices, attributes);

        ImmutableMultimap<PaoType, ? extends YukonPao> paoTypes = PaoUtils.mapPaoTypes(devices);

        // loop through each PaoType and strategy
        for (PaoType paoType : paoTypes.keySet()) {
            for (StrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    ImmutableCollection<? extends YukonPao> immutableCollection = paoTypes.get(paoType);
                    PaoMultiPointIdentifierWithUnsupported devicesWithUnsupported = attributeService.findPaoMultiPointIdentifiersForAttributes(immutableCollection, attributes);
                    boolean readable = impl.isReadable(devicesWithUnsupported.getDevicesAndPoints(), user);
                    if (readable) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
                             Set<? extends Attribute> attributes, 
                             final DeviceAttributeReadCallback delegateCallback, 
                             DeviceRequestType type, 
                             LiteYukonUser user) {
        
        log.debug("initiateRead of %s called for %.3s and %s", type, devices, attributes);
        
        // this will represent the "plan" for how all of the input devices will be read
        Map<StrategyType, Collection<PaoMultiPointIdentifier>> thePlan = 
            Maps.newEnumMap(StrategyType.class);
        
        // we need to resolve the attributes first to figure out which physical devices we'll be reading
        final List<PaoMultiPointIdentifier> devicesAndPoints =
                attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributes).getDevicesAndPoints();

        Multimap<PaoType, PaoMultiPointIdentifier> byPhysicalPaoType = ArrayListMultimap.create(1, devicesAndPoints.size());
        for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
            byPhysicalPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
        }
        
        // loop through each PaoType and strategy, add applicable PaoIdentifiers to the plan
        for (PaoType paoType : byPhysicalPaoType.keySet()) {
            StrategyType foundStrategy = null;
            for (StrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(paoType)) {
                    foundStrategy = strategy;
                    break;
                }
            }
            Collection<PaoMultiPointIdentifier> paoPointIdentifiersForType = byPhysicalPaoType.get(paoType);
            if (foundStrategy == null) {
                log.debug("no strategy found for %s devices: %.7s", paoType, paoPointIdentifiersForType);
                for (PaoMultiPointIdentifier paoMultiPoints : paoPointIdentifiersForType) {
                    MessageSourceResolvable summary = 
                        YukonMessageSourceResolvable.createDefaultWithoutCode("no strategy for " + paoType);
                    DeviceAttributeReadError strategyError = 
                        new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_STRATEGY, summary);
                    delegateCallback.receivedError(paoMultiPoints.getPao(), strategyError);
                }
            } else {
                log.debug("strategy found for %d %s devices", paoPointIdentifiersForType.size(), paoType);
                thePlan.put(foundStrategy, paoPointIdentifiersForType);
            }
        }
        
        int expectedCallbacks = thePlan.keySet().size();
        if (expectedCallbacks == 0) {
            delegateCallback.complete();
            return;
        }
        final AtomicInteger completionCounter = new AtomicInteger(expectedCallbacks);
        
        DeviceAttributeReadStrategyCallback strategyCallback = new DeviceAttributeReadStrategyCallback() {
            @Override
            public void complete() {
                int count = completionCounter.decrementAndGet();
                log.debug("one strategy for read is complete, %d remaining", count);
                if (count == 0) {
                    delegateCallback.complete();
                }
            }
            
            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                delegateCallback.receivedError(pao, error);
            }
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                delegateCallback.receivedValue(pao, value);
            }
            
            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                delegateCallback.receivedLastValue(pao);
            }
            
            @Override
            public void receivedException(DeviceAttributeReadError exception) {
                delegateCallback.receivedException(exception);
            }
            
        };
        
        for (StrategyType strategy : thePlan.keySet()) {
            DeviceAttributeReadStrategy impl = strategies.get(strategy);
            Collection<PaoMultiPointIdentifier> devicesForThisStrategy = thePlan.get(strategy);
            
            impl.initiateRead(devicesForThisStrategy, strategyCallback, type, user);
        }
    }
    
    private int initiateRead(Iterable<? extends YukonPao> devices,
                             Set<? extends Attribute> attributes, 
                             GroupCommandCompletionCallback completionCallback, 
                             DeviceRequestType type, 
                             YukonUserContext userContext) {
                        
        log.debug("initiateRead of %s called for %.3s and %s", type, devices, attributes);
        
        // this will represent the "plan" for how all of the input devices will be read
        Map<StrategyType, Collection<PaoMultiPointIdentifier>> thePlan = 
            Maps.newEnumMap(StrategyType.class);
        
        // we need to resolve the attributes first to figure out which physical devices we'll be reading
        final List<PaoMultiPointIdentifier> devicesAndPoints =
                attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributes).getDevicesAndPoints();

        Multimap<PaoType, PaoMultiPointIdentifier> byPhysicalPaoType = ArrayListMultimap.create(1, devicesAndPoints.size());
        for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
            byPhysicalPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
        }
        
        int requestCount = 0;
        for (StrategyType strategy : strategies.keySet()) {
            DeviceAttributeReadStrategy impl = strategies.get(strategy);
            Collection<PaoMultiPointIdentifier> devicesForThisStrategy = thePlan.get(strategy);
            if(devicesForThisStrategy == null){
                devicesForThisStrategy = new ArrayList<>();
            }
            for (PaoType paoType : byPhysicalPaoType.keySet()) {
                if (impl.canRead(paoType)) {
                    devicesForThisStrategy.addAll(byPhysicalPaoType.get(paoType));
                }
            }
            if(!devicesForThisStrategy.isEmpty()){
                thePlan.put(strategy, devicesForThisStrategy);
                requestCount += impl.getRequestCount(devicesForThisStrategy);
            }
            log.debug("%s strategy will be used to read %.3s", strategy, devicesForThisStrategy);
        }
             
        CommandRequestExecution execution = createExecution(type, requestCount, userContext.getYukonUser());
        completionCallback.setExecution(execution);
        
        if (thePlan.isEmpty()) {
            completionCallback.complete();
        } else {
            for (StrategyType strategy : thePlan.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                Collection<PaoMultiPointIdentifier> devicesForThisStrategy = thePlan.get(strategy);
                impl.initiateRead(devicesForThisStrategy, completionCallback, type, userContext);
            }
        }
        
        return execution.getId();
    }
    
    @Override
    public String readDeviceCollection(DeviceCollection deviceCollection, 
                                        Set<? extends Attribute> attributes, 
                                        DeviceRequestType type, 
                                        final SimpleCallback<GroupMeterReadResult> callback, 
                                        YukonUserContext userContext) {
        
        log.debug("readDeviceCollection");
        log.debug("Type:"+ type + " Attributes:" + attributes +" Devices:" + deviceCollection.getDeviceList());
        PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers = 
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);
        Map<YukonPao, Set<Attribute>> unsupportedDevices =
            paoPointIdentifiers.getUnsupportedDevices();
        log.debug("Attributes are not supported:" + attributes + " for  " + unsupportedDevices.keySet());
        Map<YukonPao, Set<Attribute>> unreadableDevices =
            getUnreadableDevices(deviceCollection.getDeviceList(), Sets.newHashSet(attributes));
        log.debug("There is no strategy found for  " + unreadableDevices.keySet());
        unsupportedDevices.putAll(unreadableDevices);
   
        // result 
        final GroupMeterReadResult groupMeterReadResult = new GroupMeterReadResult();

        // success/fail temporary groups
        final StoredDeviceGroup originalDeviceCollectionCopyGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(originalDeviceCollectionCopyGroup, deviceCollection.getDeviceList());
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices.keySet());

        // command completion callback
        GroupCommandCompletionCallback commandCompletionCallback =
            new GroupCommandCompletionCallback() {

                @Override
                public void doComplete() {
                    try {
                        CommandRequestExecution execution = getExecution();
                        if (execution != null) {
                            execution.setStopTime(new Date());
                            commandRequestExecutionDao.saveOrUpdate(execution);
                        }
                        callback.handle(groupMeterReadResult);
                    } catch (Exception e) {
                        log.debug("There was an error executing the callback", e);
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

        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        groupMeterReadResult.setFailureCollection(failureCollection);

        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        groupMeterReadResult.setUnsupportedCollection(unsupportedCollection);
        
        DeviceCollection originalDeviceCollectionCopy = deviceGroupCollectionHelper.buildDeviceCollection(originalDeviceCollectionCopyGroup);
        groupMeterReadResult.setOriginalDeviceCollectionCopy(originalDeviceCollectionCopy);
        groupMeterReadResult.setStartTime(new Date());

        /*Add to cache. The cache should be moved to this impl by YUK-12715*/
        String key = plcDeviceAttributeReadService.addResult(groupMeterReadResult);
        groupMeterReadResult.setKey(key);
        int id = initiateRead(deviceCollection.getDeviceList(), attributes, commandCompletionCallback, DeviceRequestType.GROUP_ATTRIBUTE_READ, userContext);
        CommandRequestExecutionIdentifier identifier = new CommandRequestExecutionIdentifier(id);

        groupMeterReadResult.setCommandRequestExecutionIdentifier(identifier);
        for (YukonPao pao : unsupportedDevices.keySet()) {
            CommandRequestUnsupported unsupportedCmd = new CommandRequestUnsupported();
            unsupportedCmd.setCommandRequestExecId(identifier.getCommandRequestExecutionId());
            unsupportedCmd.setDeviceId(pao.getPaoIdentifier().getPaoId());
            unsupportedCmd.setType(CommandRequestUnsupportedType.UNSUPPORTED);

            commandRequestExecutionResultDao.saveUnsupported(unsupportedCmd);
        }
        return key;
    }
    
    @Override
    public List<GroupMeterReadResult> getCompleted() {
        return plcDeviceAttributeReadService.getCompleted();
    }

    @Override
    public List<GroupMeterReadResult> getCompletedByType(DeviceRequestType type) {
        return plcDeviceAttributeReadService.getCompletedByType(type);
    }

    @Override
    public List<GroupMeterReadResult> getPending() {
        return plcDeviceAttributeReadService.getPending();
    }

    @Override
    public List<GroupMeterReadResult> getPendingByType(DeviceRequestType type) {
        return plcDeviceAttributeReadService.getPendingByType(type);
    }

    @Override
    public GroupMeterReadResult getResult(String id) {
        return plcDeviceAttributeReadService.getResult(id);
    }


    @Autowired
    public void setStrategies(List<DeviceAttributeReadStrategy> strategyList) {
        Builder<StrategyType, DeviceAttributeReadStrategy> builder = ImmutableMap.builder();
        for (DeviceAttributeReadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
        log.debug("supported strategies: %s", strategies.keySet());
    }
    

    /**
     * This method returns a list of devices that do not have a strategy.
     */
    private Map<YukonPao, Set<Attribute>> getUnreadableDevices(Iterable<? extends YukonPao> devices,
                                                                      Set<Attribute> attributes) {        
        Map<YukonPao, Set<Attribute>> unreadableDevices = new HashMap<>();
        Iterator<? extends YukonPao> iterator = devices.iterator();
        while (iterator.hasNext()) {
            YukonPao pao = iterator.next();
            pao.getPaoIdentifier().getPaoType();
            boolean isReadable = false;
            for (StrategyType strategy : strategies.keySet()) {
                DeviceAttributeReadStrategy impl = strategies.get(strategy);
                if (impl.canRead(pao.getPaoIdentifier().getPaoType())) {
                    isReadable = true;
                }
            }
            if (!isReadable) {
                unreadableDevices.put(pao, attributes);
            }
        }
        return unreadableDevices;
    }
    
    private CommandRequestExecution createExecution(DeviceRequestType type, int requestCount, LiteYukonUser user){
        CommandRequestExecutionContextId contextId = new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        CommandRequestExecution execution = new CommandRequestExecution();
        execution.setContextId(contextId.getId());
        execution.setStartTime(new Date());
        execution.setRequestCount(requestCount);
        execution.setCommandRequestExecutionType(type);
        execution.setUserName(user.getUsername());
        execution.setCommandRequestType(CommandRequestType.DEVICE);
        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
        commandRequestExecutionDao.saveOrUpdate(execution);
        return execution;
    }
}
