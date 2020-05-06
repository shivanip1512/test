package com.cannontech.common.device.config.service.impl;

import static com.cannontech.common.device.DeviceRequestType.GROUP_DEVICE_CONFIG_READ;
import static com.cannontech.common.device.DeviceRequestType.GROUP_DEVICE_CONFIG_SEND;
import static com.cannontech.common.device.DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.IN_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.OUT_OF_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNASSIGNED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNCONFIRMED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNKNOWN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNREAD;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.ASSIGN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.UNASSIGN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus.FAILURE;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus.IN_PROGRESS;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetailGroup;
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
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService, CollectionActionCancellationService {
    private final static Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);

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
    @Autowired private CommandRequestExecutionDao executionDao;
    @Autowired private DeviceConfigurationService deviceConfigurationService;

    private static final Map<DeviceRequestType, String> commands = 
            Map.of(GROUP_DEVICE_CONFIG_VERIFY,"putconfig emetcon install all verify", 
                    GROUP_DEVICE_CONFIG_READ, "getconfig install all", 
                    GROUP_DEVICE_CONFIG_SEND, "putconfig emetcon install all");

    @Override
    public int sendConfigs(DeviceCollection deviceCollection, String method,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {
        String commandString = commands.get(GROUP_DEVICE_CONFIG_SEND);
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        }
        return initiateAction(commandString, deviceCollection, LogAction.SEND, CollectionAction.SEND_CONFIG,
                GROUP_DEVICE_CONFIG_SEND, callback, context);
    }

    @Override
    public int readConfigs(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback,
            YukonUserContext context) {
        return initiateAction(commands.get(GROUP_DEVICE_CONFIG_READ), deviceCollection, LogAction.READ,
                CollectionAction.READ_CONFIG, GROUP_DEVICE_CONFIG_READ, callback, context);
    }
    
    @Override
    public int verifyConfigs(DeviceCollection deviceCollection, YukonUserContext context) {        
        VerificationSummary summary = new VerificationSummary(deviceCollection.getDeviceList());
        List<SimpleDevice> supportedDevices = new ArrayList<>(summary.supported.keySet());

        VerifyConfigCommandResult configResult = new VerifyConfigCommandResult();
        configResult.getVerifyResultsMap().putAll(summary.supported);
        
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.VERIFY_CONFIG, null,
            deviceCollection, CommandRequestType.DEVICE, GROUP_DEVICE_CONFIG_VERIFY, context);

        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, summary.unusupported);
        if (supportedDevices.isEmpty()) {
            createVerifyCallback(configResult, supportedDevices, result).complete();
        } else {
            List<CommandRequestDevice> requests = buildCommandRequests(GROUP_DEVICE_CONFIG_VERIFY, supportedDevices);
            result.getExecution().setRequestCount(requests.size());
            log.debug("updating request count {}", requests.size());
            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
            updateDeviceConfigStateToInProgress(GROUP_DEVICE_CONFIG_VERIFY, supportedDevices, result.getStartTime(),
                    result.getExecution().getId());
            commandExecutionService.execute(requests, createVerifyCallback(configResult, supportedDevices, result),
                    result.getExecution(), false, context.getYukonUser());
        }
        return result.getCacheKey();
    }

    @Override
    public VerifyConfigCommandResult verifyConfigs(List<SimpleDevice> devices, LiteYukonUser user) {
        VerifyConfigCommandResult result = new VerifyConfigCommandResult();

        VerificationSummary summary = new VerificationSummary(devices);
        Map<SimpleDevice, VerifyResult> supported = summary.supported;

        if (supported.isEmpty()) {
            return result;
        }
        result.getVerifyResultsMap().putAll(supported);

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = waitableCommandCompletionCallbackFactory
                .createWaitable(createVerifyCallback(result, supported.keySet()));
        logInitiated(devices, LogAction.VERIFY, user);
        CommandRequestExecution execution = createExecutionAndUpdateStateToInProgress(GROUP_DEVICE_CONFIG_VERIFY,
                new ArrayList<>(supported.keySet()), user);
        commandExecutionService.execute(buildCommandRequests(GROUP_DEVICE_CONFIG_VERIFY, devices), waitableCallback, execution, true, user);
        try {
            waitableCallback.waitForCompletion();
        } catch (Exception e) {
            log.error(e);
        }
        
        return result;
    }

    @Override
    public VerifyResult verifyConfig(SimpleDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singletonList(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(device);
    }

    @Override
    public CommandResultHolder readConfig(SimpleDevice device, LiteYukonUser user) throws Exception {
        return initiateAction(device, GROUP_DEVICE_CONFIG_READ, LogAction.READ, user);
    }

    @Override
    public CommandResultHolder sendConfig(SimpleDevice device, LiteYukonUser user) throws Exception {
        return initiateAction(device, GROUP_DEVICE_CONFIG_SEND, LogAction.SEND, user);
    }
   
    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.SEND_CONFIG || action == CollectionAction.READ_CONFIG;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        commandExecutionService.cancel(key, user);
    }

    
    @Override
    public void updateConfigStateForAssignAndUnassign(CollectionActionResult result) {
        CollectionActionDetailGroup successGroup = result.getDetails().get(CollectionActionDetail.SUCCESS);
        if (successGroup == null || successGroup.getDevices().getDeviceList().isEmpty()) {
            return;
        }

        List<SimpleDevice> devices = successGroup.getDevices().getDeviceList();
        log.info("Updating status for devices:{} collection action cache key:{}", devices.size(), result.getCacheKey());
        if (result.getAction() == CollectionAction.ASSIGN_CONFIG) {
            updateConfigStateForAssignAndUnassign(devices, LastAction.ASSIGN, result.getStartTime(), result.getStopTime(),
                    result.getContext().getYukonUser());
        } else if (result.getAction() == CollectionAction.UNASSIGN_CONFIG) {
            updateConfigStateForAssignAndUnassign(devices, LastAction.UNASSIGN, result.getStartTime(), result.getStopTime(),
                    result.getContext().getYukonUser());
        }
    }
    
    @Override
    public DeviceConfigState assignConfigToDevice(SimpleDevice device, DeviceConfiguration configuration,
            LiteYukonUser user) throws InvalidDeviceTypeException {
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        deviceConfigurationService.assignConfigToDevice(configuration, device, user, deviceName);
        updateConfigStateForAssignAndUnassign(List.of(device), LastAction.ASSIGN, Instant.now(), Instant.now(), user);
        return deviceConfigurationDao.getDeviceConfigStatesByDeviceId(device.getPaoIdentifier().getPaoId());
    }
    
    
    @Override
    public void unassignConfig(SimpleDevice device, LiteYukonUser user) throws InvalidDeviceTypeException {
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        deviceConfigurationService.unassignConfig(device, user, deviceName);
        updateConfigStateForAssignAndUnassign(List.of(device), LastAction.UNASSIGN, Instant.now(), Instant.now(), user);
    }
    
    /**
     * Updates device config state for for devices based on last action and entry in DeviceConfigState
     */
    private void updateConfigStateForAssignAndUnassign(List<SimpleDevice> devices, LastAction action, Instant startTime, Instant stopTime, LiteYukonUser user) {
        log.debug("Updating status for devices:{}", devices.size());
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(devices));
        Set<DeviceConfigState> states = new HashSet<>();
        List<SimpleDevice> devicesToVerify = new ArrayList<>();
        if (action == ASSIGN) {
            states = buildNewStatesForAssignAction(devices, deviceToState, startTime, stopTime);
            devicesToVerify = getDevicesToVerify(devices, deviceToState, List.of(IN_SYNC, OUT_OF_SYNC, UNASSIGNED));
        } else if (action == UNASSIGN) {
            states = buildNewStatesForUnassignAction(devices, deviceToState,  startTime, stopTime);
            devicesToVerify = getDevicesToVerify(devices, deviceToState, List.of(UNASSIGNED));
        }
        
        log.debug("{}",
                states.stream().map(newState -> "Device:" + dbCache.getAllPaosMap().get(newState.getDeviceId()).getPaoName()
                                + " New State:" + newState + " CurrentState:" + deviceToState.get(newState.getDeviceId()))
                        .collect(Collectors.joining("|")));
        
        if (!devicesToVerify.isEmpty()) {
            VerifyConfigCommandResult verifResult = verifyConfigs(devicesToVerify, user);
            log.info("Verified devices:{}",verifResult.getVerifyResultsMap().size());
        }
        
        deviceConfigurationDao.saveDeviceConfigStates(states);
    }

    private Set<DeviceConfigState> buildNewStatesForUnassignAction(List<SimpleDevice> devices,
            Map<Integer, DeviceConfigState> deviceToState, Instant stopTime, Instant startTime) {
        Set<DeviceConfigState> states = new HashSet<>();
        states.addAll(buildConfigStateByCurrentState(UNKNOWN, UNASSIGN, devices, deviceToState, startTime, stopTime, UNREAD));
        states.addAll(buildConfigStateByCurrentState(UNASSIGNED, UNASSIGN, devices, deviceToState, startTime, stopTime, IN_SYNC, OUT_OF_SYNC));
        states.addAll(buildConfigStateByCurrentState(UNCONFIRMED, UNASSIGN, devices, deviceToState, startTime, stopTime, UNCONFIRMED));
        return states;
    }

    private Set<DeviceConfigState> buildNewStatesForAssignAction(List<SimpleDevice> devices,
            Map<Integer, DeviceConfigState> deviceToState, Instant stopTime, Instant startTime) {
        Set<DeviceConfigState> states = new HashSet<>();
        states.addAll(devices.stream()
                .filter(device -> deviceToState.get(device.getDeviceId()) == null)
                .map(device -> new DeviceConfigState(device.getDeviceId(), UNREAD, ASSIGN, SUCCESS, startTime, stopTime, null))
                .collect(Collectors.toList()));
        states.addAll(buildConfigStateByCurrentState(UNREAD, ASSIGN, devices, deviceToState, startTime, stopTime, UNKNOWN));
        states.addAll(buildConfigStateByCurrentState(UNREAD, ASSIGN, devices, deviceToState, startTime, stopTime, UNREAD));
        states.addAll(buildConfigStateByCurrentState(UNCONFIRMED, ASSIGN, devices, deviceToState, startTime, stopTime, UNCONFIRMED));
        return states;
    }

    /**
     * Sends command to porter
     */
    private int initiateAction(String command, DeviceCollection deviceCollection, LogAction logAction,
            CollectionAction collectionAction, DeviceRequestType requestType,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {
        VerificationSummary summary = new VerificationSummary(deviceCollection.getDeviceList());
        List<SimpleDevice> unsupportedDevices = summary.unusupported;
        List<SimpleDevice> supportedDevices = new ArrayList<>(summary.supported.keySet());
        logInitiated(supportedDevices, logAction, context.getYukonUser());
        CollectionActionResult result = collectionActionService.createResult(collectionAction, null, deviceCollection,
            CommandRequestType.DEVICE, requestType, context);
             
        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, unsupportedDevices);
        if (supportedDevices.isEmpty()) {
            createReadSendCallback(logAction, requestType, callback, supportedDevices, result).complete();
        } else {
            List<CommandRequestDevice> requests = buildCommandRequests(requestType, supportedDevices);
            updateDeviceConfigStateToInProgress(requestType, supportedDevices, result.getStartTime(), result.getExecution().getId());
            CommandCompletionCallback<CommandRequestDevice> execCallback = createReadSendCallback(logAction, requestType, callback, supportedDevices, result);
            result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback));
            result.getExecution().setRequestCount(requests.size());
            log.debug("cache key:{} command:{} updating request count:{}", result.getCacheKey(), command, requests.size());
            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
            commandExecutionService.execute(requests, execCallback, result.getExecution(), false, context.getYukonUser());
        }
        return result.getCacheKey();
    }
    
    
    /**
     * Sends a command to porter
     */
    private CommandResultHolder initiateAction(SimpleDevice device, DeviceRequestType requestType, LogAction logAction,
            LiteYukonUser user) {
        CommandRequestExecution execution = createExecutionAndUpdateStateToInProgress(requestType,
                Collections.singletonList(device), user);
        CommandRequestDevice request = new CommandRequestDevice(commands.get(requestType),
                new SimpleDevice(device.getPaoIdentifier()));
        logInitiated(Collections.singletonList(device), logAction, user);
        CommandResultHolder resultHolder = commandExecutionService.execute(request, requestType, execution, user);
        DeviceConfigState currentState = deviceConfigurationDao.getDeviceConfigStatesByDeviceId(device.getDeviceId());
        if(resultHolder.isErrorsExist()) {
            boolean hasConfigNotCurrentError = resultHolder.getErrors().stream()
                    .anyMatch(error -> error.getDeviceError() == DeviceError.CONFIG_NOT_CURRENT);
            if (hasConfigNotCurrentError) {
                updateNewState(requestType, DeviceError.CONFIG_NOT_CURRENT, device, currentState);
            } else {
                updateNewState(requestType, Iterables.getLast(resultHolder.getErrors()).getDeviceError(), device,
                        currentState);
            }
        } else {
            updateNewState(requestType, null, device, currentState);
        }
        logCompleted(device, logAction, !resultHolder.isErrorsExist());
        return resultHolder;
    }
    
    /**
     * Creates command request execution and updates device config state to "In Progress"
     */
    private CommandRequestExecution createExecutionAndUpdateStateToInProgress(DeviceRequestType requestType,
            List<SimpleDevice> devices, LiteYukonUser user) {
        CommandRequestExecution execution = executionDao.createStartedExecution(CommandRequestType.DEVICE, requestType, 0, user);
        updateDeviceConfigStateToInProgress(requestType, devices, Instant.now(), execution.getId());
        return execution;
    }

    /**
     * Returns the list of commands for devices
     */
    private List<CommandRequestDevice> buildCommandRequests(DeviceRequestType requestType, List<SimpleDevice> devices) {
        List<CommandRequestDevice> requests = devices.stream()
                .map(d -> new CommandRequestDevice(commands.get(requestType), d))
                .collect(Collectors.toList());
        return requests;
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

    /**
     * Creates a event log entry for completed action
     */
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
    
    /**
     * Creates a event log entry for initiated action
     */
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
    
    /**
     * Builds a list of new states based on passed parameters
     * 
     * @param newState - new state to use
     * @param action - action for new states
     * @param devices - devices to check
     * @param deviceToState - used to find a current state
     * @param states - list of current states to filter devices by
     * @return list of states
     */
    private List<DeviceConfigState> buildConfigStateByCurrentState(ConfigState newState, LastAction action,
            List<SimpleDevice> devices, Map<Integer, DeviceConfigState> deviceToState, Instant startTime, Instant stopTime,
            ConfigState... states) {
        return devices.stream()
                .filter(device -> {
                    DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                    return currentState != null && currentState.getStatus() != IN_PROGRESS
                            && Lists.newArrayList(states).contains(currentState.getState());
                })
                .map(device -> new DeviceConfigState(device.getDeviceId(), newState, action, SUCCESS, startTime, stopTime, null))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns a list of devices that should be verified
     * 
     * @param devices - devices to check
     * @param deviceToState - used to find a current state
     * @param states - list of current states to filter devices by
     * @return 
     */
    private List<SimpleDevice> getDevicesToVerify(List<SimpleDevice> devices, Map<Integer, DeviceConfigState> deviceToState,
            List<ConfigState> states) {
        return devices.stream()
                .filter(device -> {
                    DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                    return currentState != null && currentState.getStatus() != IN_PROGRESS
                            && states.contains(currentState.getState());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Creates callback for Read and Send request
     */
    private CommandCompletionCallback<CommandRequestDevice> createReadSendCallback(LogAction logAction, DeviceRequestType requestType,
            SimpleCallback<CollectionActionResult> callback, List<SimpleDevice> supportedDevices,
            CollectionActionResult result) {
        return new CommandCompletionCallback<CommandRequestDevice>() {
            Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(supportedDevices));
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                logCompleted(device, logAction, true);
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
                        CollectionActionDetail.SUCCESS);
                detail.setLastValue(value);
                result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
                DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                updateNewState(requestType, null, command.getDevice(), currentState);
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                SimpleDevice device = command.getDevice();
                logCompleted(command.getDevice(), logAction, false);
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
                        CollectionActionDetail.FAILURE);
                detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
                DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                updateNewState(requestType, error.getDeviceError(), command.getDevice(), currentState);
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                result.setExecutionExceptionText(reason);
                collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
            }

            @Override
            public void complete() {
                collectionActionService.updateResult(result,
                        !result.isCanceled() ? CommandRequestExecutionStatus.COMPLETE : CommandRequestExecutionStatus.CANCELLED);
                try {
                    callback.handle(result);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        };
    }
    
    /**
     * Returns device ids for devices
     */
    private List<Integer> getDeviceIds(List<SimpleDevice> devices) {
        return devices
                .stream()
                .map(device -> device.getDeviceId())
                .collect(Collectors.toList());
    }

    /**
     * Creates callback for Verify request for collection action
     */
    private CommandCompletionCallback<CommandRequestDevice> createVerifyCallback(VerifyConfigCommandResult configResult,
            List<SimpleDevice> supportedDevices, CollectionActionResult result) {
        return new CommandCompletionCallback<CommandRequestDevice>() {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());

            Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(supportedDevices));
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
                        CollectionActionDetail.SUCCESS);
                VerifyResult discrepancy = configResult.getVerifyResultsMap().get(command.getDevice());
                detail.setLastValue(value);
                detail.setConfigName(discrepancy.getConfig().getName());
                result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
                DeviceConfigState currentState = deviceToState.get(command.getDevice().getDeviceId());
                updateNewState(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, null, command.getDevice(), currentState);
                log(command);
            }

            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                SimpleDevice device = command.getDevice();
                configResult.addError(device, error.getPorter());
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
                        CollectionActionDetail.FAILURE);
                detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                VerifyResult discrepancy = configResult.getVerifyResultsMap().get(command.getDevice());
                detail.setConfigName(discrepancy.getConfig().getName());
                if (!discrepancy.getDiscrepancies().isEmpty()) {
                    detail.setLastValue(discrepancy.getDiscrepancies().toString());
                }
                result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
                DeviceConfigState currentState = deviceToState.get(command.getDevice().getDeviceId());
                updateNewState(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, error.getDeviceError(), command.getDevice(), currentState);
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
    }
    
    /**
     * Updates device config state to "In Progress". Creates a new state if it is not found (shouldn't happen)  
     */
    private void updateDeviceConfigStateToInProgress(DeviceRequestType requestType, List<SimpleDevice> devices, Instant startTime,
            int creId) {
        if (devices.isEmpty()) {
            return;
        }
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(devices));
        Set<DeviceConfigState> states = devices.stream().map(
                device -> {
                    LastAction action = LastAction.getByRequestType(requestType);
                    DeviceConfigState existingState = deviceToState.get(device.getDeviceId());
                    if(existingState != null) {
                        existingState.setAction(action);
                        existingState.setStatus(IN_PROGRESS);
                        existingState.setActionStart(startTime);
                        existingState.setActionEnd(null);
                    } else {
                        existingState = new DeviceConfigState(device.getDeviceId(), UNKNOWN, action, IN_PROGRESS, startTime, null,
                                creId);
                    }
                    return existingState;
                }).collect(Collectors.toSet());

        log.debug("creId:{} updating DeviceConfigStates for:{}", creId, states.size());
        deviceConfigurationDao.saveDeviceConfigStates(states);
    }
    
    /**
     * Updates the entry in DeviceConfigState to a new state
     */
    private void updateNewState(DeviceRequestType requestType, DeviceError error, SimpleDevice device,
            DeviceConfigState currentState) {
        String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
        DeviceConfigState newState = buildNewState(requestType, error, device, currentState);
        if(newState == null) {
            log.error("Update state for device:{} {} failed, can't find device in DeviceConfigState table", deviceName, device);
        }
        log.debug("Device:{} New State:{} Old State:{}", deviceName, newState, currentState);
        deviceConfigurationDao.saveDeviceConfigState(newState);
    }

    /**
     * Returns new state created from existing state for read/send/verify
     */
    private DeviceConfigState buildNewState(DeviceRequestType requestType, DeviceError error, SimpleDevice device,
            DeviceConfigState currentState) {
        if (currentState == null) {
            return null;
        }
        DeviceConfigState newState = (DeviceConfigState) SerializationUtils.clone(currentState);
        newState.setActionEnd(Instant.now());
        if (error == null) {
            newState.setStatus(SUCCESS);
            if (device.getDeviceType().isPlc() && requestType == DeviceRequestType.GROUP_DEVICE_CONFIG_SEND) {
                newState.setState(UNCONFIRMED);
            } else {
                newState.setState(IN_SYNC);
            }
        } else if (error == DeviceError.CONFIG_NOT_CURRENT) {
            newState.setStatus(SUCCESS);
            newState.setState(OUT_OF_SYNC);
        } else {
            newState.setStatus(FAILURE);
        }
        return newState;
    }
    
    /**
     * Creates callback for Verify request
     */
    private CommandCompletionCallback<CommandRequestDevice> createVerifyCallback(VerifyConfigCommandResult result, Set<SimpleDevice> supported) {
        return new CommandCompletionCallback<CommandRequestDevice>() {
            Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao
                    .getDeviceConfigStatesByDeviceIds(getDeviceIds(new ArrayList<>(supported)));

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
                // result.addError(device, error.getPorter());
                logCompleted(command.getDevice(), LogAction.VERIFY, false);
                DeviceConfigState currentState = deviceToState.get(command.getDevice().getDeviceId());
                updateNewState(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, error.getDeviceError(), command.getDevice(),
                        currentState);
            }

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                result.addResultString(device, value);
                DeviceConfigState currentState = deviceToState.get(command.getDevice().getDeviceId());
                if (result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                    updateNewState(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, null, command.getDevice(), currentState);
                    logCompleted(command.getDevice(), LogAction.VERIFY, true);
                } else {
                    logCompleted(command.getDevice(), LogAction.VERIFY, false);
                }
            }
        };
    }
}