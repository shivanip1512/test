package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadServiceImpl implements DeviceAttributeReadService {

    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private List<DeviceAttributeReadStrategy> strategies;
    @Autowired private DeviceAttributeReadPlcStrategy plcStrategy;
    
    private final RecentResultsCache<GroupMeterReadResult> resultsCache = new RecentResultsCache<>();
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes,
                              LiteYukonUser user) {

        if (log.isDebugEnabled()) {
            log.debug("isReadable Attributes:" + attributes + " Devices:" + devices);
        }

        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
                getValidPointsForStrategy(devices, attributes);
        
        return !pointsForStrategy.isEmpty();
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
                             Set<? extends Attribute> attributes, 
                             DeviceAttributeReadCallback delegateCallback, 
                             DeviceRequestType type, 
                             LiteYukonUser user) {
        
        PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers = 
                attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributes);
        
        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
                getValidPointsForStrategy(devices, attributes);
        
        int requestCount = getRequestCount(pointsForStrategy);
        CommandRequestExecution execution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  type,
                                                                  requestCount,
                                                                  user);

        Set<YukonPao> unreadableDevices = getUnreadableDevices(devices);
        Set<YukonPao> unsupportedDevices = new HashSet<>();
        unsupportedDevices.addAll(paoPointIdentifiers.getUnsupportedDevices().keySet());
        
        if (log.isDebugEnabled()) {
            log.debug("initiateRead  Type:" + type + " Attributes:" + attributes + " Devices:" + devices);
            if(!unsupportedDevices.isEmpty()){
                log.debug(attributes +" not supported for  " + unsupportedDevices);
            }
            if(!unreadableDevices.isEmpty()){
                log.debug("There is no strategy found for  " + unreadableDevices);
            }
            log.debug("creId  " + execution.getId());
        }
               
        DeviceAttributeReadStrategyCallback strategyCallback =
            new ReadCallback(delegateCallback, pointsForStrategy.keySet().size(), execution);

        for (YukonPao unreadableDevice : unreadableDevices) {
            MessageSourceResolvable summary =
                YukonMessageSourceResolvable.createDefaultWithoutCode("no strategy for "
                                                                      + unreadableDevice.getPaoIdentifier()
                                                                          .getPaoType());
            DeviceAttributeReadError strategyError =
                new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_STRATEGY, summary);
            strategyCallback.receivedError(unreadableDevice.getPaoIdentifier(), strategyError);
        }

        for (YukonPao unsupportedDevice : unsupportedDevices) {
            if (!unreadableDevices.contains(unsupportedDevice)) {

                MessageSourceResolvable summary =
                    YukonMessageSourceResolvable.createDefaultWithoutCode(attributes + " not supported for "
                                                                          + unsupportedDevice.getPaoIdentifier()
                                                                              .getPaoType());
                DeviceAttributeReadError strategyError =
                    new DeviceAttributeReadError(DeviceAttributeReadErrorType.NO_ATTRIBUTE, summary);
                strategyCallback.receivedError(unsupportedDevice.getPaoIdentifier(), strategyError);
            }
     
        }
        
        if (pointsForStrategy.isEmpty()) {
            strategyCallback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                strategy.initiateRead(pointsForStrategy.get(strategy), strategyCallback, type, execution, user);
            }
        }
        
        unsupportedDevices.addAll(unreadableDevices);
        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices,
                                                         execution.getId(),
                                                         CommandRequestUnsupportedType.UNSUPPORTED);
    }
    
          
    @Override
    public String initiateRead(DeviceCollection deviceCollection, 
                                        Set<? extends Attribute> attributes, 
                                        DeviceRequestType type, 
                                        SimpleCallback<GroupMeterReadResult> callback, 
                                        LiteYukonUser user) {

        /*
         * To be removed...
         * 
         * Probably the easiest way to test if command request execution entries are correct for Ecobee strategy:
         * 
         * uncomment this line:
         * initiateRead(deviceCollection.getDeviceList(), attributes, new CollectingDeviceAttributeReadCallback(),DeviceRequestType.METER_READINGS_WIDGET_ATTRIBUTE_READ, user);
         * 
         * Using collection action select devices and do a read.
         * 
         * go to http://localhost:8080/yukon/common/commandRequestExecutionResults/list
         * 
         * You will see the entries 2 entries, "Group Attribute Read" is from this method call
         * and "Meter Readings Widget Attribute Read" from the method call on line 176
         * 
         * Group Attribute Read                    09/16/2014 10:58    09/16/2014 10:59    8   8   7   16  Completed   ats
         * Meter Readings Widget Attribute Read    09/16/2014 10:57    09/16/2014 10:59    8   8   7   16  Completed   ats
         * 
         * The entries and detail should be identical.
         */
        
       
        PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers = 
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);
        
        Set<YukonPao> unsupportedDevices = new HashSet<>();
        unsupportedDevices.addAll(paoPointIdentifiers.getUnsupportedDevices().keySet());
        
        Set<YukonPao> unreadableDevices = getUnreadableDevices(deviceCollection.getDeviceList());
                
        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy =
            getValidPointsForStrategy(deviceCollection.getDeviceList(), attributes);
        
        int requestCount = getRequestCount(pointsForStrategy);
        CommandRequestExecution execution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  type,
                                                                  requestCount,
                                                                  user);
        CommandRequestExecutionIdentifier identifier = new CommandRequestExecutionIdentifier(execution.getId());
              
        if (log.isDebugEnabled()) {
            log.debug("initiateRead  Type:" + type + " Attributes:" + attributes + " Devices:"
                      + deviceCollection.getDeviceList());
            if (!unsupportedDevices.isEmpty()) {
                log.debug(attributes + " not supported for  " + unsupportedDevices);
            }
            if (!unreadableDevices.isEmpty()) {
                log.debug("There is no strategy found for  " + unreadableDevices);
            }
            log.debug("creId  " + execution.getId());
        }
        
        StoredDeviceGroup originalDeviceCollectionCopyGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(originalDeviceCollectionCopyGroup, deviceCollection.getDeviceList());
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);
        
        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        DeviceCollection originalDeviceCollectionCopy = deviceGroupCollectionHelper.buildDeviceCollection(originalDeviceCollectionCopyGroup);
                
        GroupMeterReadResult result = new GroupMeterReadResult();
        GroupCallback groupCallback =
            new GroupCallback(callback, execution, successGroup, failureGroup, result, pointsForStrategy.keySet()
                .size());
        result.setResultHolder(groupCallback);
        result.setCallback(groupCallback);
        result.setCommandRequestExecutionIdentifier(identifier);
        result.setAttributes(attributes);
        result.setCommandRequestExecutionType(type);
        result.setDeviceCollection(deviceCollection);
        result.setSuccessCollection(successCollection);
        result.setFailureCollection(failureCollection);
        result.setUnsupportedCollection(unsupportedCollection);
        result.setOriginalDeviceCollectionCopy(originalDeviceCollectionCopy);
        result.setStartTime(new Date());

        String key = resultsCache.addResult(result);
        result.setKey(key);
                
        if (pointsForStrategy.isEmpty()) {
            groupCallback.complete();
        } else {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                strategy.initiateRead(pointsForStrategy.get(strategy), groupCallback, type, user);
            }
        }
        
        unsupportedDevices.addAll(unreadableDevices);
        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices,
                                                         execution.getId(),
                                                         CommandRequestUnsupportedType.UNSUPPORTED);
        return key;
    }
    
    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> initiateRead(DeviceCollection deviceCollection,
                                                                                        Set<? extends Attribute> attributes,
                                                                                        DeviceRequestType type,
                                                                                        CommandCompletionCallback<CommandRequestDevice> callback,
                                                                                        LiteYukonUser user,
                                                                                        RetryParameters retryParameters) {
        return plcStrategy.initiateRead(deviceCollection, attributes, type, callback, user, retryParameters);

    }
    
    private class GroupCallback extends GroupCommandCompletionCallback {

        AtomicInteger completionCounter;
        StoredDeviceGroup successGroup;
        StoredDeviceGroup failureGroup;
        SimpleCallback<GroupMeterReadResult> callback;
        GroupMeterReadResult result;

        GroupCallback(SimpleCallback<GroupMeterReadResult> callback, CommandRequestExecution execution,
                      StoredDeviceGroup successGroup, StoredDeviceGroup failureGroup, GroupMeterReadResult result, int strategyCount) {
            super.setExecution(execution);
            this.successGroup = successGroup;
            this.failureGroup = failureGroup;
            this.callback = callback;
            this.result = result;
            completionCounter = new AtomicInteger(strategyCount);
        }

        @Override
        public void complete() {
            try {
                if(log.isDebugEnabled()){
                    log.debug("Remaining strategies:"+completionCounter.get());
                }
                int count = completionCounter.decrementAndGet();
                if (count <= 0) {
                    super.complete();
                    if (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED) {
                        log.debug("All startegies complete");
                        execution.setStopTime(new Date());
                        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
                        commandRequestExecutionDao.saveOrUpdate(execution);
                    }
                    callback.handle(result);
                }

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
    }
      
    private class ReadCallback implements DeviceAttributeReadStrategyCallback {

        AtomicInteger completionCounter;
        DeviceAttributeReadCallback delegateCallback;
        CommandRequestExecution execution;

        ReadCallback(DeviceAttributeReadCallback delegateCallback, int strategyCount, CommandRequestExecution execution) {
            completionCounter = new AtomicInteger(strategyCount);
            this.delegateCallback = delegateCallback;
            this.execution = execution;
        }

        @Override
        public void complete() {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Remaining strategies:" + completionCounter.get());
                }
                int count = completionCounter.decrementAndGet();
                if (count <= 0) {
                    if (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED) {
                        log.debug("All startegies complete");
                        execution.setStopTime(new Date());
                        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
                        commandRequestExecutionDao.saveOrUpdate(execution);
                    }
                    delegateCallback.complete();
                }

            } catch (Exception e) {
                log.debug("There was an error executing the callback", e);
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

    @Override
    public GroupMeterReadResult getResult(String id) {
        return resultsCache.getResult(id);
    }

    private List<GroupMeterReadResult> filterByType(List<GroupMeterReadResult> pending, DeviceRequestType type) {
        List<GroupMeterReadResult> pendingOfType = new ArrayList<>();
        for (GroupMeterReadResult result : pending) {
            if (result.getCommandRequestExecutionType().equals(type)) {
                pendingOfType.add(result);
            }
        }
        return pendingOfType;
    } 

    /**
     * This method returns a list of devices that do not have a strategy.
     */
    private Set<YukonPao> getUnreadableDevices(Iterable<? extends YukonPao> devices) {
        
        Set<YukonPao> unreadableDevices = new HashSet<>();
        for (YukonPao pao : devices) {
            boolean canRead = false;
            for (DeviceAttributeReadStrategy strategy : strategies) {
                if (strategy.canRead(pao.getPaoIdentifier().getPaoType())) {
                    canRead = true;
                    break;
                }
            }
            if(!canRead){
                unreadableDevices.add(pao);
            }
        }

        return unreadableDevices;
    }
    
    /**
     * Returns the request count that would be send to a device 
     */
    
    private int getRequestCount(Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> pointsForStrategy){
        int requestCount = 0;
        if (!pointsForStrategy.isEmpty()) {
            for (DeviceAttributeReadStrategy strategy : pointsForStrategy.keySet()) {
                requestCount += strategy.getRequestCount(pointsForStrategy.get(strategy));
            }
        }
        return requestCount;
    }

    private Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> getValidPointsForStrategy(Iterable<? extends YukonPao> devices,
                                                                                                            Set<? extends Attribute> attributes) {

        List<PaoMultiPointIdentifier> devicesAndPoints =
            attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributes)
                .getDevicesAndPoints();

        Multimap<PaoType, PaoMultiPointIdentifier> pointsByPaoType =
            ArrayListMultimap.create(1, devicesAndPoints.size());
        for (PaoMultiPointIdentifier multiPoints : devicesAndPoints) {
            pointsByPaoType.put(multiPoints.getPao().getPaoType(), multiPoints);
        }

        Multimap<DeviceAttributeReadStrategy, PaoMultiPointIdentifier> validMetersForStrategy =
            ArrayListMultimap.create();
        for (YukonPao pao : devices) {
            for (DeviceAttributeReadStrategy strategy : strategies) {
                if (strategy.canRead(pao.getPaoIdentifier().getPaoType())) {
                    Collection<PaoMultiPointIdentifier> points =
                        pointsByPaoType.get(pao.getPaoIdentifier().getPaoType());
                    for (PaoMultiPointIdentifier point : points) {
                        validMetersForStrategy.put(strategy, point);
                    }
                }
            }
        }

        return validMetersForStrategy;
    }
}
