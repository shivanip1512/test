package com.cannontech.common.device.config.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.commands.service.GroupCommandExecutionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
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
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService {
    private final static Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);

    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private GroupCommandExecutionService groupCommandExecutionService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    @Autowired private IDatabaseCache dbCache;
    
    private String sendConfigCommand(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback,
            String command, DeviceRequestType type, LiteYukonUser user) {
        List<SimpleDevice> unsupportedDevices = new ArrayList<>();
        List<SimpleDevice> supportedDevices = new ArrayList<>();
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
        
        String key = groupCommandExecutionService.execute(supportedCollection, command, type, callback, user);
        if(type == DeviceRequestType.GROUP_DEVICE_CONFIG_SEND){
            logInitiated(supportedCollection.getDeviceList(),  LogAction.SEND, user);
        }else if(type == DeviceRequestType.GROUP_DEVICE_CONFIG_READ){
            logInitiated(supportedCollection.getDeviceList(),  LogAction.READ, user);
        }
        
        GroupCommandResult result = groupCommandExecutionService.getResult(key);
        result.setHandleUnsupported(true);
        result.setUnsupportedCollection(unsupportedCollection);
        result.setDeviceCollection(deviceCollection);
        return key;
    }

    @Override
    public String sendConfigs(DeviceCollection deviceCollection, String method,
            SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "putconfig emetcon install all";
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        }
        String resultKey = sendConfigCommand(deviceCollection, callback, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
        eventLogService.sendConfigInitiated(deviceCollection.getDeviceCount(), commandString, resultKey, user);    //after the execute so we can have the key
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
    public void logCompleted(List<SimpleDevice> devices, LogAction action, boolean isSuccessful) {
        
        int status = BooleanUtils.toInteger(isSuccessful);
        for (SimpleDevice device : devices) {
            String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
            if (action == LogAction.READ) {
                eventLogService.readConfigFromDeviceCompleted(deviceName, status);
            } else if (action == LogAction.VERIFY) {
                eventLogService.verifyConfigFromDeviceCompleted(deviceName, status);
            } else if (action == LogAction.SEND) {
                eventLogService.sendConfigToDeviceCompleted(deviceName, status);
            }
        }
    }
    
    /**
     * Logs device action (read, verify, send).
     */
    private void logInitiated(List<SimpleDevice> devices, LogAction action, LiteYukonUser user){
        for(SimpleDevice device: devices){
            String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
            if(action == LogAction.READ){
                eventLogService.readConfigFromDeviceInitiated(deviceName, user);
            }else if(action == LogAction.VERIFY){
                eventLogService.verifyConfigFromDeviceInitiated(deviceName, user);
            }else if(action == LogAction.SEND){
                eventLogService.sendConfigToDeviceInitiated(deviceName, user);
            }
        }   
    }


    @Override
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> devices, LiteYukonUser user) {
        eventLogService.verifyConfigInitiated(Iterables.size(devices), user);
        return doVerifyConfigs(devices, user);
    }

    private VerifyConfigCommandResult doVerifyConfigs(Iterable<? extends YukonDevice> devices, LiteYukonUser user) {   
        final VerifyConfigCommandResult result = new VerifyConfigCommandResult();
        final String commandString = "putconfig emetcon install all verify";
        List<YukonDevice> deviceList = Lists.newArrayList(devices);

        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper =
            new ObjectMapper<YukonDevice, CommandRequestDevice>() {
                @Override
                public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                    return new CommandRequestDevice(commandString, new SimpleDevice(from.getPaoIdentifier()));
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

        List<CommandRequestDevice> requests = new MappingList<>(deviceList, objectMapper);

        CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
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
                    String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
                    // This was commented out to prevent adding the final summary result status message to the out-of-sync parts list
                    //result.addError(device, error.getPorter());
                    result.handleFailure(device);
                    eventLogService.verifyConfigFromDeviceCompleted(deviceName, 0);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    SimpleDevice device = command.getDevice();
                    String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
                    result.addResultString(device, value);
                    if (result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                        result.handleSuccess(device);
                        eventLogService.verifyConfigFromDeviceCompleted(deviceName, 1);
                    } else {
                        result.handleFailure(device);
                        eventLogService.verifyConfigFromDeviceCompleted(deviceName, 0);
                    }
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    result.setExceptionReason(reason);
                }
            };

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback =
            waitableCommandCompletionCallbackFactory.createWaitable(commandCompletionCallback);

        logInitiated(deviceList.stream()
                               .map(x -> new SimpleDevice(x))
                               .collect(Collectors.toList()), LogAction.VERIFY, user);
            
        commandRequestService.execute(requests, waitableCallback, DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, user);
        try {
            waitableCallback.waitForCompletion();
            logCompleted(result.getSuccessList(), LogAction.VERIFY, true);
            logCompleted(result.getFailureList(), LogAction.VERIFY, false);
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
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.verifyConfigFromDeviceInitiated(deviceName, user);

        VerifyConfigCommandResult verifyConfigResult = doVerifyConfigs(Collections.singleton(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(new SimpleDevice(device));
    }

    @Override
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.readConfigFromDeviceInitiated(deviceName, user);
        String commandString = "getconfig install all";
        CommandRequestDevice request = new CommandRequestDevice(commandString, new SimpleDevice(device.getPaoIdentifier()));
        CommandResultHolder resultHolder = commandRequestService.execute(request,
            DeviceRequestType.GROUP_DEVICE_CONFIG_READ, user);
        
        int status = BooleanUtils.toInteger(!resultHolder.isErrorsExist());
        eventLogService.readConfigFromDeviceCompleted(deviceName, status);
        return resultHolder;
    }

    @Override
    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "putconfig emetcon install all";
        
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.sendConfigToDeviceInitiated(deviceName, user);
        CommandRequestDevice request = new CommandRequestDevice(commandString, new SimpleDevice(device.getPaoIdentifier()));
        CommandResultHolder resultHolder =
                commandRequestService.execute(request, DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
        
        int status = BooleanUtils.toInteger(!resultHolder.isErrorsExist());
        eventLogService.sendConfigToDeviceCompleted(deviceName, status);
        return resultHolder;
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
