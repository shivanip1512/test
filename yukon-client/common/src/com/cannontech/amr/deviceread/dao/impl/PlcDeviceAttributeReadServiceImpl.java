package com.cannontech.amr.deviceread.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.device.commands.impl.CommandRequestRetryExecutor;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableSet;

public class PlcDeviceAttributeReadServiceImpl implements PlcDeviceAttributeReadService {
    private final static Logger log = YukonLogManager.getLogger(PlcDeviceAttributeReadServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;

    private final RecentResultsCache<GroupMeterReadResult> resultsCache = new RecentResultsCache<>();

    @Override
    public boolean isReadable(YukonPao device, Set<? extends Attribute> attributes, LiteYukonUser user) {
        log.debug("Validating Readability for " + attributes + " on device " + device + " for " + user);

        List<PaoMultiPointIdentifier> paoPointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(ImmutableSet.of(device), attributes).getDevicesAndPoints();

        return isReadable(paoPointIdentifiers, user);
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> paoPointIdentifiers, LiteYukonUser user) {
        return meterReadCommandGeneratorService.isReadable(paoPointIdentifiers);
    }

    @Override
    public CommandResultHolder readMeter(YukonDevice device, Set<? extends Attribute> attributes,
            DeviceRequestType type, LiteYukonUser user) {
        log.info("Reading " + attributes + " on device " + device + " for " + user);
        List<PaoMultiPointIdentifier> paoPointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(ImmutableSet.of(device), attributes).getDevicesAndPoints();

        List<CommandRequestDevice> commandRequests =
            meterReadCommandGeneratorService.getCommandRequests(paoPointIdentifiers);
        if (commandRequests.isEmpty()) {
            throw new RuntimeException("It isn't possible to read " + attributes + " for  " + device);
        }

        CommandRequestExecutionTemplate<CommandRequestDevice> executionTemplate =
            commandRequestDeviceExecutor.getExecutionTemplate(type, user);
        CollectingCommandCompletionCallback callback = new CollectingCommandCompletionCallback();
        WaitableCommandCompletionCallback<Object> waitableCallback =
            waitableCommandCompletionCallbackFactory.createWaitable(callback);
        executionTemplate.execute(commandRequests, waitableCallback);
        try {
            waitableCallback.waitForCompletion();
            return callback;
        } catch (InterruptedException | TimeoutException e) {
            throw new MeterReadRequestException(e);
        }
    }

    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> backgroundReadDeviceCollection(
            final Iterable<PaoMultiPointIdentifier> pointsToRead, DeviceRequestType type,
            CommandCompletionCallback<CommandRequestDevice> callback, LiteYukonUser user,
            RetryParameters retryParameters) {
        List<CommandRequestDevice> commandRequests = meterReadCommandGeneratorService.getCommandRequests(pointsToRead);
        CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor =
            new CommandRequestRetryExecutor<>(commandRequestDeviceExecutor, retryParameters);
        CommandRequestExecutionObjects<CommandRequestDevice> executionObjects =
            retryExecutor.execute(commandRequests, callback, type, user);
        
        return executionObjects;
    }

    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> backgroundReadDeviceCollection(
            DeviceCollection deviceCollection, Set<? extends Attribute> attributes, DeviceRequestType type,
            CommandCompletionCallback<CommandRequestDevice> callback, LiteYukonUser user,
            RetryParameters retryParameters) {
        
        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, 0, user);
        
        PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(deviceCollection, attributes);
        
        Set<YukonPao> unsupportedDevices = new HashSet<>();
        
        // MCTs that support the attributes
        List<PaoMultiPointIdentifier> supportedDevices = new ArrayList<>();
        
        unsupportedDevices.addAll(paoPointIdentifiers.getUnsupportedDevices().keySet());
        
        log.debug("Attributes are not supported:" + attributes + " for  " + unsupportedDevices);
        
        for(PaoMultiPointIdentifier identifier: paoPointIdentifiers.getDevicesAndPoints()){
            if(identifier.getPao().getPaoType().isMct()){
                supportedDevices.add(identifier);
            }else{
                unsupportedDevices.add(identifier.getPao());
            }
        }
        
        for (YukonPao device : unsupportedDevices) {
            CommandRequestUnsupported unsupported = new CommandRequestUnsupported();
            unsupported.setCommandRequestExecId(execution.getId());
            unsupported.setDeviceId(device.getPaoIdentifier().getPaoId());
            unsupported.setType(CommandRequestUnsupportedType.UNSUPPORTED);
            commandRequestExecutionResultDao.saveUnsupported(unsupported);
        }
        
        CommandRequestExecutionObjects<CommandRequestDevice> executionObjects = null;
        if (!supportedDevices.isEmpty()) {
            List<CommandRequestDevice> commandRequests =
                meterReadCommandGeneratorService.getCommandRequests(supportedDevices);
            CommandRequestRetryExecutor<CommandRequestDevice> retryExecutor =
                new CommandRequestRetryExecutor<>(commandRequestDeviceExecutor, retryParameters);
            executionObjects = retryExecutor.execute(commandRequests, callback, execution, user);
        } else {
            execution.setStopTime(new Date());
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.COMPLETE);
            commandRequestExecutionDao.saveOrUpdate(execution);
            CommandRequestExecutionContextId commandRequestExecutionContextId = new CommandRequestExecutionContextId(execution.getContextId());
            executionObjects = new CommandRequestExecutionObjects<CommandRequestDevice>(null, null, commandRequestExecutionContextId);
        }

        return executionObjects;
    }

    @Override
    public List<GroupMeterReadResult> getCompleted() {
        return resultsCache.getCompleted();
    }

    @Override
    public String addResult(GroupMeterReadResult groupMeterReadResult) {
        return resultsCache.addResult(groupMeterReadResult);
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
}
