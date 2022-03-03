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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
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
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.model.DeviceConfigState;
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
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService, CollectionActionCancellationService {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CommandRequestExecutionDao executionDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceDao deviceDao;

    private static final BiMap<DeviceRequestType, String> commands = new ImmutableBiMap.Builder<DeviceRequestType, String>()
            .put(GROUP_DEVICE_CONFIG_VERIFY, "putconfig install all verify")
            .put(GROUP_DEVICE_CONFIG_READ, "getconfig install all")
            .put(GROUP_DEVICE_CONFIG_SEND, "putconfig install all")
            .build();

    
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
        
    private VerifyConfigCommandResult verifyConfigs(List<SimpleDevice> devices, LiteYukonUser user) {
        Map<Integer, LightDeviceConfiguration> configurations = deviceConfigurationDao.getConfigurations(getDeviceIds(devices));
        DeviceSummary summary = new DeviceSummary(devices, configurations);

        VerifyConfigCommandResult configResult = getInitializedConfigResult(configurations, summary.supported);

        if (summary.supported.isEmpty()) {
            return configResult;
        }
        
        CommandRequestExecution execution = executionDao.createStartedExecution(CommandRequestType.DEVICE,
                GROUP_DEVICE_CONFIG_VERIFY, summary.supported.size(), user);
        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = waitableCommandCompletionCallbackFactory
                .createWaitable(createVerifyCallback(configResult, summary.supported));
        logInitiated(devices, LogAction.VERIFY, user);
        commandExecutionService.execute(buildCommandRequests(GROUP_DEVICE_CONFIG_VERIFY, summary.supported), waitableCallback,
                execution, true, user);
        try {
            waitableCallback.waitForCompletion();
        } catch (Exception e) {
            log.error(e);
        }

        return configResult;
    }

    @Override
    public VerifyResult verifyConfig(SimpleDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singletonList(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(device);
    }

    @Override
    public void readConfig(SimpleDevice device, LiteYukonUser user) {
        initiateAction(device, GROUP_DEVICE_CONFIG_READ, LogAction.READ, user);
    }

    @Override
    public void sendConfig(SimpleDevice device, LiteYukonUser user) {
        initiateAction(device, GROUP_DEVICE_CONFIG_SEND, LogAction.SEND, user);
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
        updateConfigStateForAssignAndUnassign(devices,
                result.getAction() == CollectionAction.ASSIGN_CONFIG ? LastAction.ASSIGN : LastAction.UNASSIGN,
                result.getStartTime(), result.getStopTime(),
                result.getContext().getYukonUser());
    }

    @Override
    public void processCommandRequest(List<Request> commandRequests) {
        commandRequests.forEach(request -> {
            SimpleDevice device = new SimpleDevice(dbCache.getAllPaosMap().get(request.getDeviceID()));
            if (isConfigCommandSupported(device)) {
                DeviceRequestType requestType = getRequestTypeByCommand(request.getCommandString());
                if (requestType != null) {
                    updateStatusToInProgress(requestType, List.of(request.getDeviceID()), Instant.now(), null);
                }
            }
        });
    }
    
    @Override
    public void processCommandReturn(Return response) {
        SimpleDevice device = new SimpleDevice(dbCache.getAllPaosMap().get(response.getDeviceID()));
        if (!isConfigCommandSupported(device)) {
            return;
        }
        DeviceRequestType requestType = getRequestTypeByCommand(response.getCommandString());
        if (requestType != null) {
            DeviceConfigState state = deviceConfigurationDao.getDeviceConfigStateByDeviceId(response.getDeviceID());
            DeviceError error = response.isError() ? deviceErrorTranslatorDao.translateErrorCode(response.getStatus())
                    .getError() : null;
            updateState(requestType, error, device, state);
        }
    }
    
    /**
     * Returns DeviceRequestType matched to the command
     */
    private DeviceRequestType getRequestTypeByCommand(String commandString) {
        String formattedString = commandString
                .replace("update", "")
                .replace("noqueue", "")
                .strip()
                .replaceAll("\\s{2,}"," ");
        return commands.inverse().get(formattedString);
    }
    
    /**
     * Returns in progress message for the log file
     */
    private String getInProgressMessage(YukonUserContext context) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(context);
        return accessor.getMessage("yukon.web.widgets.configWidget.actionInProgress");
    }
    
    /**
     * Updates device config state for for devices based on last action and entry in DeviceConfigState
     */
    private void updateConfigStateForAssignAndUnassign(List<SimpleDevice> allDevices, LastAction action, Instant startTime, Instant stopTime, LiteYukonUser user) {
        log.debug("Updating status for devices:{}", allDevices.size());
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(allDevices));
        Set<DeviceConfigState> states = new HashSet<>();
        List<SimpleDevice> devicesToVerify = new ArrayList<>();
        if (action == ASSIGN) {
            List<SimpleDevice> disabledDevices = deviceDao.getDisabledDevices(Lists.transform(allDevices, SimpleDevice::getDeviceId));
            List<SimpleDevice> enabledDevices = new ArrayList<>();
            enabledDevices.addAll(allDevices);
            enabledDevices.removeAll(disabledDevices);
            states = buildNewStatesForAssignAction(enabledDevices, deviceToState, startTime, stopTime, disabledDevices);
            devicesToVerify = getDevicesToVerify(enabledDevices, deviceToState, List.of(IN_SYNC, OUT_OF_SYNC, UNASSIGNED));
        } else if (action == UNASSIGN) {
            states = buildNewStatesForUnassignAction(allDevices, deviceToState,  startTime, stopTime);
            devicesToVerify = getDevicesToVerify(allDevices, deviceToState, List.of(UNASSIGNED));
        }
        
        if (log.isDebugEnabled()) {
            log.debug("{}",
                states.stream().map(newState -> "Device:" + dbCache.getAllPaosMap().get(newState.getDeviceId()).getPaoName()
                                + " New State:" + newState + " CurrentState:" + deviceToState.get(newState.getDeviceId()))
                        .collect(Collectors.joining("|")));
        }
        
        if (!devicesToVerify.isEmpty()) {
            VerifyConfigCommandResult verifResult = verifyConfigs(devicesToVerify, user);
            
            verifResult.getVerifyResultsMap()
                .forEach((device, verifyResult) ->
                    updateState(
                            DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, 
                            verifyResult.getError(), 
                            device, 
                            deviceToState.get(device.getDeviceId())));

            log.info("Verified devices:{}",verifResult.getVerifyResultsMap().size());
        }
        
        deviceConfigurationDao.saveDeviceConfigStates(states);
    }

    private Set<DeviceConfigState> buildNewStatesForUnassignAction(List<SimpleDevice> devices,
            Map<Integer, DeviceConfigState> deviceToState, Instant startTime, Instant stopTime) {
        Set<DeviceConfigState> states = new HashSet<>();
        states.addAll(buildConfigStateByCurrentState(UNKNOWN, UNASSIGN, devices, deviceToState, startTime, stopTime, UNREAD));
        states.addAll(buildConfigStateByCurrentState(UNASSIGNED, UNASSIGN, devices, deviceToState, startTime, stopTime, IN_SYNC, OUT_OF_SYNC));
        states.addAll(buildConfigStateByCurrentState(UNCONFIRMED, UNASSIGN, devices, deviceToState, startTime, stopTime, UNCONFIRMED));
        return states;
    }

    private Set<DeviceConfigState> buildNewStatesForAssignAction(List<SimpleDevice> enabledDevices,
            Map<Integer, DeviceConfigState> deviceToState, Instant startTime, Instant stopTime,
            List<SimpleDevice> disabledDevices) {
        Set<DeviceConfigState> states = new HashSet<>();
        if (!disabledDevices.isEmpty()) {
            // add disabled devices with the state UNKNOWN
            states.addAll(disabledDevices.stream()
                    .map(device -> new DeviceConfigState(device.getDeviceId(), UNKNOWN, ASSIGN, SUCCESS, startTime, stopTime, null))
                    .collect(Collectors.toList()));
        }
        
        if(!enabledDevices.isEmpty()) {
            states.addAll(enabledDevices.stream()
                    .filter(device -> deviceToState.get(device.getDeviceId()) == null)
                    .map(device -> new DeviceConfigState(device.getDeviceId(), UNREAD, ASSIGN, SUCCESS, startTime, stopTime, null))
                    .collect(Collectors.toList()));
            states.addAll(buildConfigStateByCurrentState(UNREAD, ASSIGN, enabledDevices, deviceToState, startTime, stopTime, UNKNOWN));
            states.addAll(buildConfigStateByCurrentState(UNREAD, ASSIGN, enabledDevices, deviceToState, startTime, stopTime, UNREAD));
            states.addAll(
                    buildConfigStateByCurrentState(UNCONFIRMED, ASSIGN, enabledDevices, deviceToState, startTime, stopTime, UNCONFIRMED));
        }
        return states;
    }

    
    private final class DeviceSummary {
        List<SimpleDevice> supported = new ArrayList<>();
        List<SimpleDevice> unsupported = new ArrayList<>();
        List<SimpleDevice> inProgress = new ArrayList<>();

        DeviceSummary(List<SimpleDevice> devices, Map<Integer, LightDeviceConfiguration> configurations) {
            List<SimpleDevice> supportedDevices = devices.stream().filter(device -> {
                LightDeviceConfiguration configuration = configurations.get(device.getDeviceId());
                if (isConfigCommandSupported(device) && configuration != null
                        && deviceConfigurationDao.isTypeSupportedByConfiguration(configuration,
                                device.getPaoIdentifier().getPaoType())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            
            inProgress.addAll(deviceConfigurationDao.getInProgressDevices(getDeviceIds(supportedDevices)));
            supported.addAll(ListUtils.subtract(supportedDevices, inProgress));
            unsupported.addAll(ListUtils.subtract(devices, supportedDevices));           
            log.debug("supported:{} unsupported:{} inProgress:{} total:{}", supported.size(), unsupported.size(), inProgress.size(), devices.size());
        }
    }
    
    /**
     * Creates a pre-populated result with devices and configurations to keep track of the values received from Porter
     */
    private VerifyConfigCommandResult getInitializedConfigResult(Map<Integer, LightDeviceConfiguration> configurations,
            List<SimpleDevice> supportedDevices) {
        if (supportedDevices.isEmpty()) {
            return new VerifyConfigCommandResult();
        }
        Map<PaoIdentifier, DisplayablePao> displayableDeviceLookup = paoLoadingService
                .getDisplayableDeviceLookup(supportedDevices);
        Map<SimpleDevice, VerifyResult> devices = supportedDevices.stream()
                .collect(Collectors.toMap(device -> device,
                        device -> new VerifyResult(displayableDeviceLookup.get(device.getPaoIdentifier()),
                                configurations.get(device.getDeviceId()))));
        return new VerifyConfigCommandResult(devices);
    }
    
    /**
     * Sends command to porter
     */
    private int initiateAction(String command, DeviceCollection deviceCollection, LogAction logAction,
            CollectionAction collectionAction, DeviceRequestType requestType,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {
        CollectionActionResult result = collectionActionService.createResult(collectionAction, null, deviceCollection,
            CommandRequestType.DEVICE, requestType, context);
        Map<Integer, LightDeviceConfiguration> configurations = deviceConfigurationDao
                .getConfigurations(getDeviceIds(deviceCollection.getDeviceList()));
        DeviceSummary summary  = new DeviceSummary(deviceCollection.getDeviceList(), configurations );
        
        logInitiated(summary.supported, logAction, context.getYukonUser());
        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, summary.unsupported);
        collectionActionService.addUnsupportedToResult(CollectionActionDetail.INVALID_STATE, result, summary.inProgress,
                getInProgressMessage(context));

        if (summary.supported.isEmpty()) {
            createReadSendCallback(logAction, requestType, callback, summary.supported, result).complete();
        } else {
            List<CommandRequestDevice> requests = buildCommandRequests(requestType, summary.supported);
            updateStatusToInProgress(requestType, getDeviceIds(summary.supported), result.getStartTime(), result.getExecution().getId());
            CommandCompletionCallback<CommandRequestDevice> execCallback = createReadSendCallback(logAction, requestType, callback, summary.supported, result);
            result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback));
            result.getExecution().setRequestCount(requests.size());
            log.debug("cache key:{} command:{} updating request count:{}", result.getCacheKey(), command, requests.size());
            executionDao.saveOrUpdate(result.getExecution());
            commandExecutionService.execute(requests, execCallback, result.getExecution(), false, context.getYukonUser());
        }
        return result.getCacheKey();
    }
    
    
    /**
     * Sends a command to porter
     */
    private void initiateAction(SimpleDevice device, DeviceRequestType requestType, LogAction logAction,
            LiteYukonUser user) {
        CommandRequestDevice request = new CommandRequestDevice(commands.get(requestType),
                new SimpleDevice(device.getPaoIdentifier()));
        logInitiated(Collections.singletonList(device), logAction, user);
        CommandRequestExecution execution = createExecutionAndUpdateStateToInProgress(requestType,
                Collections.singletonList(device), user);
        commandExecutionService.execute(List.of(request), new CommandCompletionCallback<CommandRequestDevice>() {
            Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao
                    .getDeviceConfigStatesByDeviceIds(List.of(device.getDeviceId()));

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                logCompleted(device, logAction, true);
                DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                updateState(requestType, null, command.getDevice(), currentState);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                logCompleted(command.getDevice(), logAction, false);
                DeviceConfigState currentState = deviceToState.get(device.getDeviceId());
                updateState(requestType, error.getDeviceError(), command.getDevice(), currentState);
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                logCompleted(device, logAction, false);
                deviceConfigurationDao.failInProgressDevices(List.of(device.getDeviceId()));
            }
        }, execution, true, user);
    }

    /**
     * Creates command request execution and updates device config state to "In Progress"
     */
    private CommandRequestExecution createExecutionAndUpdateStateToInProgress(DeviceRequestType requestType,
            List<SimpleDevice> devices, LiteYukonUser user) {
        CommandRequestExecution execution = executionDao.createStartedExecution(CommandRequestType.DEVICE, requestType, devices.size(), user);
        updateStatusToInProgress(requestType, getDeviceIds(devices), Instant.now(), execution.getId());
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
            eventLogService.validateConfigOnDeviceCompleted(deviceName, status);
        } else if (action == LogAction.VERIFY) {
            eventLogService.verifyConfigFromDeviceCompleted(deviceName, status);
        } else if (action == LogAction.SEND) {
            eventLogService.uploadConfigToDeviceCompleted(deviceName, status);
        }
    }
    
    /**
     * Creates a event log entry for initiated action
     */
    private void logInitiated(List<SimpleDevice> devices, LogAction action, LiteYukonUser user) {
        for (SimpleDevice device : devices) {
            String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
            if (action == LogAction.READ) {
                eventLogService.validateConfigOnDeviceInitiated(deviceName, user);
            } else if (action == LogAction.VERIFY) {
                eventLogService.verifyConfigFromDeviceInitiated(deviceName, user);
            } else if (action == LogAction.SEND) {
                eventLogService.uploadConfigToDeviceInitiated(deviceName, user);
            }
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
                    return currentState != null && currentState.getLastActionStatus() != IN_PROGRESS
                            && Lists.newArrayList(states).contains(currentState.getCurrentState());
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
                    return currentState != null && currentState.getLastActionStatus() != IN_PROGRESS
                            && states.contains(currentState.getCurrentState());
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
                updateState(requestType, null, command.getDevice(), currentState);
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
                updateState(requestType, error.getDeviceError(), command.getDevice(), currentState);
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                supportedDevices.forEach(device -> logCompleted(device, logAction, false));
                result.setExecutionExceptionText(reason);
                collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
                deviceConfigurationDao.failInProgressDevices(getDeviceIds(supportedDevices));
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
     * Updates device config state to "In Progress". Creates a new state if it is not found (shouldn't happen)
     */
    private void updateStatusToInProgress(DeviceRequestType requestType, List<Integer> deviceIds, Instant startTime,
            Integer creId) {
        if (deviceIds.isEmpty()) {
            return;
        }
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(deviceIds);
        Set<DeviceConfigState> states = deviceIds.stream().map(
                deviceId -> {
                    LastAction action = LastAction.getByRequestType(requestType);
                    DeviceConfigState existingState = deviceToState.get(deviceId);
                    if (existingState != null) {
                        existingState.setLastAction(action);
                        existingState.setLastActionStatus(IN_PROGRESS);
                        existingState.setActionStart(startTime);
                        existingState.setActionEnd(null);
                        existingState.setCreId(creId);
                    } else {
                        existingState = new DeviceConfigState(deviceId, UNKNOWN, action, IN_PROGRESS, startTime, null, creId);
                    }
                    return existingState;
                }).collect(Collectors.toSet());

        log.debug("creId:{} updating DeviceConfigStates for:{}", creId, states.size());
        deviceConfigurationDao.saveDeviceConfigStates(states);
    }
    
    /**
     * Updates the entry in DeviceConfigState to a new state
     */
    private void updateState(DeviceRequestType requestType, DeviceError error, SimpleDevice device,
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
        DeviceConfigState newState = SerializationUtils.clone(currentState);
        newState.setActionEnd(Instant.now());
        if (error == null) {
            newState.setLastActionStatus(SUCCESS);
            if (device.getDeviceType().isPlc() && requestType == DeviceRequestType.GROUP_DEVICE_CONFIG_SEND) {
                newState.setCurrentState(UNCONFIRMED);
            } else {
                newState.setCurrentState(IN_SYNC);
            }
        } else if (error == DeviceError.CONFIG_NOT_CURRENT) {
            newState.setLastActionStatus(FAILURE);
            newState.setCurrentState(OUT_OF_SYNC);
        } else {
            if (requestType == DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY) {
                newState.setCurrentState(OUT_OF_SYNC);
            }
            newState.setLastActionStatus(FAILURE);
        }
        return newState;
    }
    
    /**
     * Creates callback for Verify request
     */
    private CommandCompletionCallback<CommandRequestDevice> createVerifyCallback(VerifyConfigCommandResult result,
            List<SimpleDevice> supported) {
        return new CommandCompletionCallback<CommandRequestDevice>() {
            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                result.addResultString(device, value);
            }

            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                SimpleDevice device = command.getDevice();
                result.addDiscrepancy(device, error.getPorter());
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                //  Ignore error.getPorter() here - do not add the final summary result status message to the out-of-sync parts list
                result.setError(command.getDevice(), error.getDeviceError());
                logCompleted(command.getDevice(), LogAction.VERIFY, false);
            }
            
            @Override
            public void processingExceptionOccurred(String reason) {
                supported.forEach(device -> logCompleted(device, LogAction.VERIFY, false));
                deviceConfigurationDao.failInProgressDevices(getDeviceIds(supported));
            }

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                result.addResultString(device, value);
                var successful = result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty();
                logCompleted(command.getDevice(), LogAction.VERIFY, successful);
            }
        };
    }
}