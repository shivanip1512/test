package com.cannontech.common.device.config.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService, CollectionActionCancellationService {
    private final static Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);

    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    
    private static final String VERIFY_COMMAND = "putconfig emetcon install all verify";

    private int initiateAction(String command, DeviceCollection deviceCollection, LogAction logAction,
            CollectionAction collectionAction, DeviceRequestType requestType,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {
        VerificationSummary summary = new VerificationSummary(deviceCollection.getDeviceList());
        List<SimpleDevice> unsupportedDevices = summary.unusupported;
        List<SimpleDevice> supportedDevices = new ArrayList<>(summary.supported.keySet());
        logInitiated(supportedDevices, logAction, context.getYukonUser());
        CollectionActionResult result = collectionActionService.createResult(collectionAction, null, deviceCollection,
            CommandRequestType.DEVICE, requestType, context);
        CommandCompletionCallback<CommandRequestDevice> execCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    logCompleted(command.getDevice(), logAction, true);
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.SUCCESS);
                    detail.setLastValue(value);
                    result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    logCompleted(command.getDevice(), logAction, false);
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.FAILURE);
                    detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                    result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
                }

                @Override
                public void processingExceptionOccurred(String reason) {
                    result.setExecutionExceptionText(reason);
                    collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
                }
                
                @Override
                public void complete() {
                    collectionActionService.updateResult(result, !result.isCanceled()
                        ? CommandRequestExecutionStatus.COMPLETE : CommandRequestExecutionStatus.CANCELLED);
                    try {
                        callback.handle(result);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            };

        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, unsupportedDevices);
        if (supportedDevices.isEmpty()) {
            execCallback.complete();
        } else {
            List<CommandRequestDevice> requests = supportedDevices.stream().map(
                device -> new CommandRequestDevice(command, new SimpleDevice(device.getPaoIdentifier()))).collect(
                    Collectors.toList());
            result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback)); 
            result.getExecution().setRequestCount(requests.size());
            log.debug("updating request count =" + requests.size());
            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
            commandExecutionService.execute(requests, execCallback, result.getExecution(), false, context.getYukonUser());
        }
        return result.getCacheKey();
    }
    
    @Override
    public int sendConfigs(DeviceCollection deviceCollection, String method,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {
        String commandString = "putconfig emetcon install all";
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        }
        int cacheKey = initiateAction(commandString, deviceCollection, LogAction.SEND, CollectionAction.SEND_CONFIG,
            DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, callback, context);
        return cacheKey;
    }

    @Override
    public int readConfigs(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback,
            YukonUserContext context) {
        String commandString = "getconfig install all";

        int cacheKey = initiateAction(commandString, deviceCollection, LogAction.READ, CollectionAction.READ_CONFIG,
            DeviceRequestType.GROUP_DEVICE_CONFIG_READ, callback, context);
        return cacheKey;
    }


    private void logCompleted(SimpleDevice device, LogAction action, boolean isSuccessful) {
        int status = BooleanUtils.toInteger(isSuccessful);
        String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
        if (action == LogAction.READ) {
            eventLogService.readConfigFromDeviceCompleted(deviceName, status);
        } else if (action == LogAction.VERIFY) {
            eventLogService.verifyConfigFromDeviceCompleted(deviceName, status);
        } else if (action == LogAction.SEND) {
            eventLogService.sendConfigToDeviceCompleted(deviceName, status);
        }
    }
    
    private void logInitiated(List<SimpleDevice> devices, LogAction action, LiteYukonUser user) {
        for (SimpleDevice device : devices) {
            String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
            if (action == LogAction.READ) {
                eventLogService.readConfigFromDeviceInitiated(deviceName, user);
            } else if (action == LogAction.VERIFY) {
                eventLogService.verifyConfigFromDeviceInitiated(deviceName, user);
            } else if (action == LogAction.SEND) {
                eventLogService.sendConfigToDeviceInitiated(deviceName, user);
            }
        }
    }

    private class VerificationSummary {
        List<SimpleDevice> unusupported = new ArrayList<>();
        Map<SimpleDevice, VerifyResult> supported = new HashMap<>();

        public VerificationSummary(List<SimpleDevice> devices) {
            Map<PaoIdentifier, DisplayablePao> displayableDeviceLookup =
                paoLoadingService.getDisplayableDeviceLookup(devices);
            unusupported.addAll(devices);
            devices.forEach(device -> {
                if (isConfigCommandSupported(device)) {
                    DisplayablePao displayableDevice = displayableDeviceLookup.get(device.getPaoIdentifier());
                    LightDeviceConfiguration configuration = deviceConfigurationDao.findConfigurationForDevice(device);
                    if (configuration != null && deviceConfigurationDao.isTypeSupportedByConfiguration(configuration,
                        device.getPaoIdentifier().getPaoType())) {
                        VerifyResult verifyResult = new VerifyResult(displayableDevice);
                        verifyResult.setConfig(configuration);
                        supported.put(device, verifyResult);
                        unusupported.remove(device);
                    }
                }
            });
        }
    }
    
    @Override
    public int verifyConfigs(DeviceCollection deviceCollection, YukonUserContext context) {        
        VerificationSummary summary = new VerificationSummary(deviceCollection.getDeviceList());
        List<SimpleDevice> supportedDevices = new ArrayList<>(summary.supported.keySet());

        VerifyConfigCommandResult configResult = new VerifyConfigCommandResult();
        configResult.getVerifyResultsMap().putAll(summary.supported);
        
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.VERIFY_CONFIG, null,
            deviceCollection, CommandRequestType.DEVICE, DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, context);
        
        CommandCompletionCallback<CommandRequestDevice> execCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.SUCCESS);
                    VerifyResult discrepancy = configResult.getVerifyResultsMap().get(command.getDevice());
                    detail.setLastValue(value);
                    detail.setConfigName(discrepancy.getConfig().getName());
                    result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
                    log(command);
                }

                @Override
                public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    SimpleDevice device = command.getDevice();
                    configResult.addError(device, error.getPorter());
                }
                
                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.FAILURE);
                    detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                    VerifyResult discrepancy = configResult.getVerifyResultsMap().get(command.getDevice());
                    detail.setConfigName(discrepancy.getConfig().getName());
                    if (!discrepancy.getDiscrepancies().isEmpty()) {
                        detail.setLastValue(discrepancy.getDiscrepancies().toString());
                    }
                    result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
                    log(command);
                }

                @Override
                public void processingExceptionOccurred(String reason) {
                    result.setExecutionExceptionText(reason);
                    collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
                }
                
                @Override
                public void complete() {
                    collectionActionService.updateResult(result, CommandRequestExecutionStatus.COMPLETE);
                }
                
                private void log(CommandRequestDevice command) {
                    if (configResult.getVerifyResultsMap().get(command.getDevice()).getDiscrepancies().isEmpty()) {
                        logCompleted(command.getDevice(), LogAction.VERIFY, true);
                    } else {
                        logCompleted(command.getDevice(), LogAction.VERIFY, false);
                    }
                }
            };

        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, summary.unusupported);
        if (supportedDevices.isEmpty()) {
            execCallback.complete();
        } else {
            List<CommandRequestDevice> requests = supportedDevices.stream().map(
                device -> new CommandRequestDevice(VERIFY_COMMAND, new SimpleDevice(device.getPaoIdentifier()))).collect(
                    Collectors.toList());
            result.getExecution().setRequestCount(requests.size());
            log.debug("updating request count =" + requests.size());
            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
            commandExecutionService.execute(requests, execCallback, result.getExecution(), false, context.getYukonUser());
        }
        return result.getCacheKey();
    }

    @Override
    public VerifyConfigCommandResult verifyConfigs(List<SimpleDevice> devices, LiteYukonUser user) {
        return doVerifyConfigs(devices, user);
    }

    private VerifyConfigCommandResult doVerifyConfigs(List<SimpleDevice> devices, LiteYukonUser user) {   
        VerifyConfigCommandResult result = new VerifyConfigCommandResult();
        
        VerificationSummary summary = new VerificationSummary(devices);
        Map<SimpleDevice, VerifyResult> supported = summary.supported;
        
        if(supported.isEmpty()) {
            return result;
        }
        result.getVerifyResultsMap().putAll(supported);
        
        List<CommandRequestDevice> requests = supported.keySet().stream()
                .map(d -> new CommandRequestDevice(VERIFY_COMMAND, d))
                .collect(Collectors.toList());

        CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
                @Override
                public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                    SimpleDevice device = command.getDevice();
                    result.addResultString(device, value);
                }

                @Override
                public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    SimpleDevice device = command.getDevice();
                    result.addError(device, error.getPorter());
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    // This was commented out to prevent adding the final summary result status message to the out-of-sync parts list
                    //result.addError(device, error.getPorter());
                    logCompleted(command.getDevice(), LogAction.VERIFY, false);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    SimpleDevice device = command.getDevice();
                    result.addResultString(device, value);
                    if (result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                        logCompleted(command.getDevice(), LogAction.VERIFY, true);
                    } else {
                        logCompleted(command.getDevice(), LogAction.VERIFY, false);
                    }
                }
            };

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback =
            waitableCommandCompletionCallbackFactory.createWaitable(commandCompletionCallback);

        logInitiated(Lists.newArrayList(supported.keySet()), LogAction.VERIFY, user);
            
        commandRequestService.execute(requests, waitableCallback, DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, user);
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {
            log.error(e);
        } catch (TimeoutException e) {
            log.error(e);
        }
        return result;
    }

    @Override
    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user) {
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.verifyConfigFromDeviceInitiated(deviceName, user);

        VerifyConfigCommandResult verifyConfigResult = doVerifyConfigs(Lists.newArrayList(new SimpleDevice(device)), user);
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

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.SEND_CONFIG || action == CollectionAction.READ_CONFIG;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        commandRequestService.cancel(key, user);
    }
}
