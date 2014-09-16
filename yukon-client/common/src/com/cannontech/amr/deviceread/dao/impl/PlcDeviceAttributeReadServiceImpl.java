package com.cannontech.amr.deviceread.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
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
}
