package com.cannontech.common.device.config.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService {
    private final static Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);

    @Autowired private CommandRequestDeviceExecutor commandRequestExecutor;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private GroupCommandExecutor groupCommandExecutor;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;

    private String sendConfigCommand(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback,
            String command, DeviceRequestType type, LiteYukonUser user) {
        List<SimpleDevice> unsupportedDevices = new ArrayList<SimpleDevice>();
        List<SimpleDevice> supportedDevices = new ArrayList<SimpleDevice>();
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
            if (!isConfigCommandSupported(device)) {
                unsupportedDevices.add(device);
            } else {
                // hit the db to find out I guess.
                LightDeviceConfiguration configuration = deviceConfigurationDao.findConfigurationForDevice(device);
                if (configuration != null
                    && deviceConfigurationDao.isTypeSupportedByConfiguration(configuration, device.getDeviceType())) {
                    supportedDevices.add(device);
                } else {
                    unsupportedDevices.add(device);
                }
            }
        }

        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup supportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);
        deviceGroupMemberEditorDao.addDevices(supportedGroup, supportedDevices);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        DeviceCollection supportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(supportedGroup);

        String key = groupCommandExecutor.execute(supportedCollection, command, type, callback, user);
        GroupCommandResult result = groupCommandExecutor.getResult(key);
        result.setHandleUnsupported(true);
        result.setUnsupportedCollection(unsupportedCollection);

        return key;
    }

    @Override
    public String sendConfigs(DeviceCollection deviceCollection, String method,
            SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "putconfig emetcon install all";
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        }
        String resultKey = sendConfigCommand(deviceCollection, callback, commandString, 
                                             DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
        eventLogService.sendConfigInitiated(1, commandString, resultKey, user);    //after the execute so we can have the key
        return resultKey;
    }

    @Override
    public String readConfigs(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback,
            LiteYukonUser user) {
        String commandString = "getconfig install all";

        String resultKey = sendConfigCommand(deviceCollection, callback, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_READ, user);
        eventLogService.readConfigInitiated(deviceCollection.getDeviceCount(), resultKey, user);    // after execute call so we can get the key.
        return resultKey;
    }

    @Override
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> devices, LiteYukonUser user) {
        final VerifyConfigCommandResult result = new VerifyConfigCommandResult();
        final String commandString = "putconfig emetcon install all verify";
        List<YukonDevice> deviceList = Lists.newArrayList(devices);

        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper =
            new ObjectMapper<YukonDevice, CommandRequestDevice>() {
                @Override
                public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                    return buildStandardRequest(from, commandString);
                }
            };

        Map<PaoIdentifier, DisplayablePao> displayableDeviceLookup =
            paoLoadingService.getDisplayableDeviceLookup(devices);
        for (YukonDevice device : devices) {
            if (!isConfigCommandSupported(device)) {
                deviceList.remove(device);
                result.getUnsupportedList().add(new SimpleDevice(device));
            } else {
                // hit the db to find out I guess.
                DisplayablePao displayableDevice = displayableDeviceLookup.get(device.getPaoIdentifier());
                LightDeviceConfiguration configuration = deviceConfigurationDao.findConfigurationForDevice(device);
                if (configuration != null
                    && deviceConfigurationDao.isTypeSupportedByConfiguration(configuration,
                        device.getPaoIdentifier().getPaoType())) {
                    VerifyResult verifyResult = new VerifyResult(displayableDevice);
                    verifyResult.setConfig(configuration);
                    result.getVerifyResultsMap().put(new SimpleDevice(device.getPaoIdentifier()), verifyResult);
                } else {
                    deviceList.remove(device);
                    result.getUnsupportedList().add(new SimpleDevice(device));
                }
            }
        }

        List<CommandRequestDevice> requests =
            new MappingList<YukonDevice, CommandRequestDevice>(deviceList, objectMapper);

        CommandCompletionCallbackAdapter<CommandRequestDevice> commandCompletionCallback =
            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
                @Override
                public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                    SimpleDevice device = command.getDevice();
                    result.addResultString(device, value);
                }

                @Override
                public void
                    receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    SimpleDevice device = command.getDevice();
                    result.addError(device, error.getPorter());
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    SimpleDevice device = command.getDevice();
                    result.addError(device, error.getPorter());
                    result.handleFailure(device);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    SimpleDevice device = command.getDevice();
                    result.addResultString(device, value);
                    if (result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                        result.handleSuccess(device);
                    } else {
                        result.handleFailure(device);
                    }
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    result.setExceptionReason(reason);
                }
            };

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback =
            waitableCommandCompletionCallbackFactory.createWaitable(commandCompletionCallback);

        eventLogService.verifyConfigInitiated(deviceList.size(), user);
        commandRequestExecutor.execute(requests, waitableCallback, DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, user);
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {
            result.setExceptionReason(e.getMessage());
            log.error(e);
        } catch (TimeoutException e) {
            result.setExceptionReason("Operation Timed Out");
            log.error(e);
        }
        return result;
    }

    @Override
    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singleton(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(new SimpleDevice(device));
    }

    @Override
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        eventLogService.readConfigInitiated(1, "", user);
        String commandString = "getconfig install all";
        CommandResultHolder resultHolder =
            commandRequestExecutor.execute(device, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_READ, user);
        return resultHolder;
    }

    @Override
    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "putconfig emetcon install all";
        
        eventLogService.sendConfigInitiated(1, commandString, "", user);
        CommandResultHolder resultHolder =
            commandRequestExecutor.execute(device, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
        return resultHolder;
    }

    private CommandRequestDevice buildStandardRequest(YukonDevice device, final String command) {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setDevice(new SimpleDevice(device.getPaoIdentifier()));

        request.setCommandCallback(new PorterCommandCallback(command));
        return request;
    }

    /**
     * Helper method to save on some database hits.
     * Returns false if any
     * - PaoTag.DEVICE_CONFIGURATION is not supported
     * - ConfigurationType is DNP (not supported for read, send, verify commands)
     * Else returns true.
     */
    private boolean isConfigCommandSupported(YukonDevice device) {
        if (!paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), PaoTag.DEVICE_CONFIGURATION)) {
            return false;
        }
        if (paoDefinitionDao.isDnpConfigurationType(device.getPaoIdentifier().getPaoType())) {
            return false;
        }
        return true;
    }
}
