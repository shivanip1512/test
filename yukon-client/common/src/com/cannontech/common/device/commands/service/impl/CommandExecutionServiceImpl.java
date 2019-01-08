package com.cannontech.common.device.commands.service.impl;

import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.CANCELLED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.COMPLETE;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.FAILED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.STARTED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandPriority;
import com.cannontech.common.device.commands.CommandRequestBase;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.ExecutionParameters;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.CommandRequestExecutorEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PorterRequestCancelService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.Lists; 

public class CommandExecutionServiceImpl implements CommandExecutionService {
    private final static Logger log = YukonLogManager.getLogger(CommandExecutionServiceImpl.class);
    private final static Random random = new Random();

    private final static int CANCEL_PRIORITY = 8;

    @Autowired private PorterRequestCancelService porterRequestCancelService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CommandRequestExecutorEventLogService commandRequestExecutorEventLogService;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableFactory;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private CommandPermissionConverter commandPermissionConverter;
    @Autowired private @Qualifier("main") Executor executor;
    @Autowired private @Qualifier("porter") BasicServerConnection porterConnection;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private Set<Permission> loggableCommandPermissions = new HashSet<>();

    private final Map<CommandCompletionCallback<? extends CommandRequestBase>, CommandResultMessageListener> msgListeners =
        new ConcurrentHashMap<>();

    @Override
    public int execute(CollectionAction action, LinkedHashMap<String, String> inputs, DeviceCollection collection,
            String command, CommandRequestType commandRequestType, DeviceRequestType deviceRequestType,
            SimpleCallback<CollectionActionResult> callback, YukonUserContext context) {

        CollectionActionResult result = collectionActionService.createResult(action, inputs, collection,
            commandRequestType, deviceRequestType, context);
        CommandCompletionCallback<CommandRequestDevice> execCallback =
            new CommandCompletionCallback<CommandRequestDevice>() {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
                List<SimpleDevice> devices = Collections.synchronizedList(collection.getDeviceList());

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice());
                    detail.setValue(value);
                    result.appendToLogWithoutAddingToGroup(detail);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.SUCCESS);
                    result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
                    devices.remove(command.getDevice());
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    CollectionActionLogDetail detail =
                        new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.FAILURE);
                    detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                    result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
                    devices.remove(command.getDevice());
                }

                @Override
                public void processingExceptionOccurred(String reason) {
                    result.setExecutionExceptionText(reason);
                    collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
                }

                @Override
                public void complete() {
                    if (!result.isComplete()) {
                        collectionActionService.updateResult(result, !result.isCanceled()
                            ? CommandRequestExecutionStatus.COMPLETE : CommandRequestExecutionStatus.CANCELLED);
                        if (callback != null) {
                            try {
                                callback.handle(result);
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }
                    }
                }
                
                @Override
                public void cancel() {
                    complete();
                }
            };
        List<CommandRequestBase> commands =
            collection.getDeviceList().stream().map(device -> new CommandRequestDevice(command, device)).collect(
                Collectors.toList());
        result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback));
        execute(commands, execCallback, result.getExecution(), false, context.getYukonUser());
        return result.getCacheKey();
    }

    @Override
    public CommandResultHolder execute(CommandRequestBase command, DeviceRequestType type, LiteYukonUser user) {
        CollectingCommandCompletionCallback callback = new CollectingCommandCompletionCallback();
        WaitableCommandCompletionCallback<? extends CommandRequestBase> waitableCallback =
            waitableFactory.createWaitable(callback, command.getPaoType());
        ExecutionParameters params = createExecParams(type, user, false);
        CommandRequestExecution execution = createAndSaveExecution(params, Lists.newArrayList(command));
        CommandRequestExecutionIdentifier identifier =
            sendToPorter(Lists.newArrayList(command), waitableCallback, params, execution);
        callback.setCommandRequestExecutionIdentifier(identifier);
        try {
            waitableCallback.waitForCompletion();
            log.debug("Execution " + execution.getId() + " for command " + command + " is complete.");
        } catch (InterruptedException | TimeoutException e) {
            String error = "Execution " + execution.getId() + "for command " + command + " timed out.";
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
            MessageSourceResolvable detail =
                YukonMessageSourceResolvable.createSingleCode("yukon.common.device.attributeRead.general.timeout");
            SpecificDeviceErrorDescription errorDesc = new SpecificDeviceErrorDescription(errorDescription, detail);
            callback.getErrors().add(errorDesc);
            callback.processingExceptionOccurred(error);
            log.error(error, e);
        }
        return callback;

    }

    @Override
    public CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, DeviceRequestType type,
            LiteYukonUser user) {
        if (CollectionUtils.isEmpty(commands)) {
            log.error("The requested operation is not supported as there is not commands to execute.");
            throw new UnsupportedOperationException("Received no commands for execution");
        }
        ExecutionParameters params = createExecParams(type, user, false);
        CommandRequestExecution execution = createAndSaveExecution(params, commands);
        sendToPorter(commands, callback, params, execution);
        return new CommandRequestExecutionIdentifier(execution.getId());
    }

    @Override
    public CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, CommandRequestExecution execution,
            boolean updateExecutionStatus, LiteYukonUser user) {
        ExecutionParameters params = createExecParams(new CommandRequestExecutionContextId(execution.getContextId()),
            execution.getCommandRequestExecutionType(), updateExecutionStatus, null, user);
        sendToPorter(commands, callback, params, execution);
        return new CommandRequestExecutionIdentifier(execution.getId());
    }

    @Override
    public CommandRequestExecutionIdentifier execute(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, CommandRequestExecution execution,
            boolean updateExecutionStatus, Boolean noqueue, LiteYukonUser user) {
        ExecutionParameters params = createExecParams(new CommandRequestExecutionContextId(execution.getContextId()),
            execution.getCommandRequestExecutionType(), updateExecutionStatus, noqueue, user);
        sendToPorter(commands, callback, params, execution);
        return new CommandRequestExecutionIdentifier(execution.getId());
    }

    /**
     * Creates execution parameters
     */
    private ExecutionParameters createExecParams(DeviceRequestType type, LiteYukonUser user,
            boolean updateExecutionStatus) {
        CommandRequestExecutionContextId contextId =
            new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        return createExecParams(contextId, type, updateExecutionStatus, null, user);
    }

    /**
     * Creates execution parameters for existing execution
     */
    private ExecutionParameters createExecParams(CommandRequestExecutionContextId contextId, DeviceRequestType type,
           boolean updateExecutionStatus, Boolean noqueue, LiteYukonUser user) {
        ExecutionParameters params = new ExecutionParameters(contextId, type, updateExecutionStatus, noqueue, user);
        overrideCommandPriority(params);
        return params;
    }
    
    /**
     * Sends commands to porter
     * 
     * @param commands - commands to send
     * @param callback - callback to notify the user of the processed results
     * @param params - params to apply when command to porter is generated
     * @param execution - info about initiated action
     * @return execution id
     */
    private CommandRequestExecutionIdentifier sendToPorter(List<? extends CommandRequestBase> commands,
            CommandCompletionCallback<? extends CommandRequestBase> callback, ExecutionParameters params,
            CommandRequestExecution execution) {
       
        log.debug(buildLogString(execution) + " params:" + params + " requests:" + commands.size());
        executor.execute(() -> {
            int groupMessageId = random.nextInt();
            List<RequestHolder> commandRequests =
                commands.stream().map(command -> new RequestHolder(command, groupMessageId, params))
                    .collect(Collectors.toList());

            log.debug(buildLogString(execution) + " created message group id:" + groupMessageId);
            CommandResultMessageListener listener = new CommandResultMessageListener(commandRequests, callback,
                groupMessageId, execution, params.updateExecutionStatus());

            msgListeners.put(callback, listener);
            porterConnection.addMessageListener(listener);

            boolean nothingWritten = true;
            String commandToSend = null;
            // ids of devices that were send to porter
            List<Integer> deviceIdsProcessed = new ArrayList<>();
            try {
                for (RequestHolder requestHolder : commandRequests) {
                    if (listener.isCanceled()) {
                        log.debug("Processing canceled for execution id:" + execution.getId() + "."
                            + (commandRequests.size() - commands.size()) + " were canceled.");
                        break;
                    }
                    commandToSend = requestHolder.request.getCommandString();
                    porterConnection.write(requestHolder.request);
                    nothingWritten = false;
                    deviceIdsProcessed.add(requestHolder.request.getDeviceID());
                    logCommand(requestHolder.request, params.getUser());
                    if (log.isDebugEnabled()) {
                        log.debug(buildLogString(execution) + " sent request to porter:" + requestHolder.request);
                    }
                }
                
                if (nothingWritten && !listener.isCanceled()) {
                    completeRequestAndRemoveListeners(listener, COMPLETE, params.updateExecutionStatus());
                }
                
            } catch (ConnectionException e) {
                String error = "No porter connection.";
                log.error(error, e);
                callback.processingExceptionOccurred(error);
                commandRequestExecutorEventLogService.commandFailedToTransmit(execution.getId(), params.getContextId(),
                    params.getType(), commandToSend, error, params.getUser());
                completeRequestAndRemoveListeners(listener, FAILED, true);
            } catch (Exception e) {
                log.error(buildLogString(execution), e);
                callback.processingExceptionOccurred(e.getMessage());
                commandRequestExecutorEventLogService.commandFailedToTransmit(execution.getId(), params.getContextId(),
                    params.getType(), commandToSend, e.getMessage(), params.getUser());
                completeRequestAndRemoveListeners(listener, FAILED, true);
            } finally {
                commandRequestExecutionResultDao.saveExecutionRequest(execution.getId(), deviceIdsProcessed);
                listener.getCommandsAreWritingLatch().countDown();
            }
        });

        return new CommandRequestExecutionIdentifier(execution.getId());
    }
    
    /**
     * Completes request and removes listener
     * @param listener - listener to remove
     * @param status - new execution status
     * @param updateExecutionStatus - if true updates execution
     */
    private void completeRequestAndRemoveListeners(CommandResultMessageListener listener,
            CommandRequestExecutionStatus status, boolean updateExecutionStatus) {
        log.debug(buildLogString(listener.execution) + " Removing porter message listener: " + listener);
        listener.callback.complete();
        porterConnection.removeMessageListener(listener);
        msgListeners.remove(listener.callback);
        if (updateExecutionStatus) {
            log.debug(buildLogString(listener.execution) + "  Setting execution status to  " + status);
            completeCommandRequestExecutionRecord(listener.execution, status);
        }
    }

    private final class RequestHolder {
        public RequestHolder(CommandRequestBase command, int groupMessageId, ExecutionParameters params) {
            this.request = command.generateRequest(params, groupMessageId);
            this.command = command;
        }

        public Request request;
        public CommandRequestBase command;
    }

    private CommandRequestExecution createAndSaveExecution(ExecutionParameters params,
            List<? extends CommandRequestBase> commands) {
        CommandRequestType commandRequestType = commands.get(0).getCommandRequestType();
        LiteYukonUser user = params.getUser();
        CommandRequestExecution execution = new CommandRequestExecution();
        execution.setContextId(params.getContextId());
        execution.setStartTime(new Date());
        execution.setRequestCount(commands.size());
        execution.setCommandRequestExecutionType(params.getType());
        execution.setUserName(user.getUsername());
        execution.setCommandRequestType(commandRequestType);
        execution.setCommandRequestExecutionStatus(STARTED);

        commandRequestExecutionDao.saveOrUpdate(execution);
        return execution;
    }

    /**
     * Overrides commands priority based on master.cfg parameter
     */
    private void overrideCommandPriority(ExecutionParameters params) {
        int commandPriority =
            configurationSource.getInteger("OVERRIDE_PRIORITY_" + params.getType(), params.getPriority());
        if (!CommandPriority.isCommandPriorityValid(commandPriority)) {
            log.warn("System has received a new priority for " + params.getType()
                + ", but cannot use the value because it is invalid.  The system will revert to using the default priority value until a valid priority is supplied.  Please fix the priority value and try again.");
        } else {
            params.overridePriority(commandPriority);
        }
    }

    private void completeCommandRequestExecutionRecord(CommandRequestExecution commandRequestExecution,
            CommandRequestExecutionStatus executionStatus) {
        commandRequestExecution.setStopTime(new Date());
        commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
        commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
    }

    private void logCommand(Request request, LiteYukonUser user) {
        String commandString = request.getCommandString();
        Permission permission = commandPermissionConverter.getPermission(commandString);
        if (loggableCommandPermissions.contains(permission)) {
            new SystemLogHelper(PointTypes.SYS_PID_SYSTEM).log("Manual: " + request.getCommandString(),
                "ID: " + request.getDeviceID() + ", Route: " + request.getRouteID(), user);
        }
    }

    private final class CommandResultMessageListener implements MessageListener {
        private final CommandCompletionCallback<CommandRequestBase> callback;
        private final Map<Long, CommandRequestBase> pendingUserMessageIds;
        private final int groupMessageId;
        private volatile boolean canceled = false;
        private final CountDownLatch commandsAreWritingLatch = new CountDownLatch(1);
        private final CommandRequestExecution execution;
        private final boolean updateExecutionStatus;

        private CommandResultMessageListener(List<RequestHolder> requests,
                CommandCompletionCallback<? extends CommandRequestBase> callback, int groupMessageId,
                CommandRequestExecution execution, boolean updateExecutionStatus) {
           
            this.callback = (CommandCompletionCallback<CommandRequestBase>) callback;
            this.groupMessageId = groupMessageId;
            this.execution = execution;
            this.updateExecutionStatus = updateExecutionStatus;

            pendingUserMessageIds = new HashMap<>(requests.size());
            for (RequestHolder requestHolder : requests) {
                pendingUserMessageIds.put(requestHolder.request.getUserMessageID(), requestHolder.command);
            }
        }

        @Override
        public synchronized void messageReceived(MessageEvent e) {

            boolean debug = log.isDebugEnabled();

            Message message = e.getMessage();

            if (message instanceof Return) {

                Return rtn = (Return) message;
                long userMessageId = rtn.getUserMessageID();

                if (pendingUserMessageIds.containsKey(userMessageId)) {

                    CommandRequestBase command = pendingUserMessageIds.get(userMessageId);
                    if (debug) {
                        log.debug("Got return message " + rtn + " for my " + command + " on " + this);
                    }

                    // check the status message and create an error if necessary
                    SpecificDeviceErrorDescription specificErrorDescription = null;
                    int status = rtn.getStatus();
                    if (status != 0) {
                        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(status);
                        MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                            "yukon.common.device.errorDetail", rtn.getResultString());
                        specificErrorDescription =
                            new SpecificDeviceErrorDescription(errorDescription, rtn.getResultString(), detail);
                        if (debug) {
                            log.debug("Calling receivedError on " + callback + " for " + rtn);
                        }
                    }

                    // grab point data and give it to the callback
                    List<Message> messages = rtn.getMessages();
                    for (Message m : messages) {
                        if (m instanceof PointData) {
                            PointData pd = (PointData) m;
                            if (debug) {
                                log.debug("Calling receivedValue on " + callback + " for " + rtn);
                            }
                            callback.receivedValue(command, pd);
                        } else {
                            handleUnknownReturn(userMessageId, m);
                        }
                    }

                    // last results
                    if (rtn.getExpectMore() == 0) {

                        if (specificErrorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedLastError on " + callback + " for " + rtn);
                            }
                            callback.receivedLastError(command, specificErrorDescription);

                        } else {
                            log.debug("Calling receivedLastResultString on " + callback + " for " + rtn);
                            callback.receivedLastResultString(command, rtn.getResultString());

                        }

                        // insert CommandRequestExecutionResult record
                        saveCommandRequestExecutionResult(this.execution, command, status,
                            rtn.getCommandString());

                        pendingUserMessageIds.remove(userMessageId);

                        // intermediate results
                    } else {
                        if (specificErrorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedIntermediateError on " + callback + " for " + rtn);
                            }
                            callback.receivedIntermediateError(command, specificErrorDescription);
                        } else {
                            if (debug) {
                                log.debug("Calling receivedIntermediateResultString on " + callback + " for " + rtn);
                            }
                            callback.receivedIntermediateResultString(command, rtn.getResultString());
                        }
                    }

                }

                if (pendingUserMessageIds.isEmpty()) {
                    completeRequestAndRemoveListeners(this, COMPLETE, updateExecutionStatus);
                }
            }
        }

        private synchronized void handleUnknownReturn(long userMessageID, Object aResult) {
            // figure out what the command was
            String origCommand = "unknown-command";
            CommandRequestBase command = pendingUserMessageIds.get(userMessageID);
            if (command != null) {
                origCommand = command.toString();
            }
            log.warn("received a message in the result vector that wasn't a PointData: " + aResult.getClass().toString()
                + "; original command was: " + origCommand + " message was: " + aResult);
        }

        public int getGroupMessageId() {
            return groupMessageId;
        }

        public void setCanceled() {
            this.canceled = true;
        }

        public boolean isCanceled() {
            return this.canceled;
        }

        public CountDownLatch getCommandsAreWritingLatch() {
            return commandsAreWritingLatch;
        }

        @Override
        public synchronized String toString() {
            ToStringCreator tsc = new ToStringCreator(this);
            tsc.append("groupMessageId", groupMessageId);
            tsc.append("canceled", canceled);
            tsc.append("callback", callback);
            tsc.append("pendingUserMessageIds", pendingUserMessageIds.keySet());
            return tsc.toString();
        }
    }

    private void saveCommandRequestExecutionResult(CommandRequestExecution execution, CommandRequestBase command,
            int status, String returnMsgCommand) {
        CommandRequestExecutionResult result = new CommandRequestExecutionResult();
        result.setCommandRequestExecutionId(execution.getId());
        result.setCommand(returnMsgCommand);
        result.setCompleteTime(new Date());
        result.setErrorCode(status);
        command.applyIdsToCommandRequestExecutionResult(result);
        commandRequestExecutionResultDao.saveOrUpdate(result);
    }

    @Override
    public long cancelExecution(CommandCompletionCallback<?> callback, LiteYukonUser user,
            boolean updateExecutionStatus) {

        CommandResultMessageListener listener = msgListeners != null ? msgListeners.get(callback) : null;

        if (listener == null) {
            log.info("No message listener found for callback=" + callback);
            return 0;
        }
        listener.setCanceled();
        try {
            if (!listener.getCommandsAreWritingLatch().await(5, TimeUnit.MINUTES)) {
                log.debug(buildCancelLogString(listener) + " Timeout waiting for latch");
            } else {
                log.debug(buildCancelLogString(listener) + " Latch await over");
            }
        } catch (InterruptedException e) {
            log.error(e);
        }

        long commandsCanceled = 0;

        try {
            log.info(buildCancelLogString(listener));
            commandsCanceled = porterRequestCancelService.cancelRequests(listener.getGroupMessageId(), CANCEL_PRIORITY);
        } catch (ConnectionException e) {
            log.error("Porter is not responding", e);
        }

        callback.cancel();
        completeRequestAndRemoveListeners(listener, CANCELLED, updateExecutionStatus);

        return commandsCanceled;
    }
    
    @Required
    public void setLoggableCommandPermissions(Set<Permission> loggableCommandPermissions) {
        this.loggableCommandPermissions = loggableCommandPermissions;
    }
    
    private String buildLogString(CommandRequestExecution execution) {
        return  "Processing execution id:" + execution.getId();
    }
    
    private String buildCancelLogString(CommandResultMessageListener listener) {
        return "Canceling execution id:" + listener.execution.getId() + " groupMessageId:"
            + listener.getGroupMessageId();
    }

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.SEND_COMMAND;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = collectionActionService.getCachedResult(key);
        if (result != null) {
            result.setCanceled(true);
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELING);
            result.getCancellationCallbacks(StrategyType.PORTER).forEach(callback ->{
                cancelExecution(callback.getCommandCompletionCallback(), user, false);
            });
        }
    }
}
