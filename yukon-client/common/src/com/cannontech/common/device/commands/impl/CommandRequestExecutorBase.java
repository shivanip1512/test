package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.service.PorterRequestCancelService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

/**
 * Abstract base class for CommandRequestExecutor
 * @param <T> - Type of command request this executor will execute
 */
@ManagedResource
public abstract class CommandRequestExecutorBase<T> implements
        CommandRequestExecutor<T> {
    private BasicServerConnection porterConnection;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private CommandPermissionConverter commandPermissionConverter;
    private PorterRequestCancelService porterRequestCancelService;
    private Set<Permission> loggableCommandPermissions = new HashSet<Permission>();
    private ConfigurationSource configurationSource;
    
    private int defaultForegroundPriority = 14;
    private int defaultBackgroundPriority = 8;

    private Map<CommandCompletionCallback<? super T>, CommandResultMessageListener> msgListeners = new HashMap<CommandCompletionCallback<? super T>, CommandResultMessageListener>();
    
    Logger log = YukonLogManager.getLogger(CommandRequestExecutorBase.class);
    private int betweenResultsMaxDelay = 60;
    private int totalMaxDelay = 180;

    protected int getDefaultForegroundPriority() {
        return defaultForegroundPriority;
    }

    protected int getDefaultBackgroundPriority() {
        return defaultBackgroundPriority;
    }
    
    @PostConstruct
    public void initialize() {
        betweenResultsMaxDelay = configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_BETWEEN_RESULTS_MAX_DELAY", betweenResultsMaxDelay);
        totalMaxDelay = configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_TOTAL_MAX_DELAY", totalMaxDelay);
    }

    private final class CommandResultMessageListener implements MessageListener {
        private final CommandCompletionCallback<? super T> callback;
        private Map<Long, T> pendingUserMessageIds;
        private int groupMessageId;

        private CommandResultMessageListener(List<RequestHolder> requests,
                CommandCompletionCallback<? super T> callback,
                int groupMessageId) {
            
            this.callback = callback;
            this.groupMessageId = groupMessageId;
            
            pendingUserMessageIds = new HashMap<Long, T>(requests.size());
            for (RequestHolder requestHolder : requests) {
                pendingUserMessageIds.put(requestHolder.request.getUserMessageID(), requestHolder.command);
            }
        }

        public synchronized void messageReceived(MessageEvent e) {
            boolean debug = log.isDebugEnabled();
            Message message = e.getMessage();
            if (message instanceof Return) {
                Return retMessage = (Return) message;
                long userMessageId = retMessage.getUserMessageID();
                if (pendingUserMessageIds.containsKey(userMessageId)) {
                    T command = pendingUserMessageIds.get(userMessageId);
                    if (debug) {
                        log.debug("Got return message " + retMessage + " for my " + command + " on " + this);
                    }
                    // this is one of ours, check the status message and create an error if necessary 
                    DeviceErrorDescription errorDescription = null;
                    int status = retMessage.getStatus();
                    if (status != 0) {
                        errorDescription = deviceErrorTranslatorDao.translateErrorCode(status);
                        errorDescription.setPorter(retMessage.getResultString());
                        if (debug) {
                            log.debug("Calling receivedError on " + callback + " for " + retMessage);
                        }
                    }
                    Vector<?> resultVector = retMessage.getVector();
                    for (Object aResult : resultVector) {
                        if (aResult instanceof PointData) {
                            PointData pData = (PointData) aResult;
                            if (debug) {
                                log.debug("Calling receivedValue on " + callback + " for " + retMessage);
                            }
                            callback.receivedValue(command, pData);
                        } else {
                            handleUnknownReturn(userMessageId, aResult);
                        }
                    }

                    if (retMessage.getExpectMore() == 0) {
                        if (errorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedLastError on " + callback + " for " + retMessage);
                            }
                            callback.receivedLastError(command, errorDescription);
                        } else {
                            log.debug("Calling receivedLastResultString on " + callback + " for " + retMessage);
                            callback.receivedLastResultString(command, retMessage.getResultString());
                        }
                        
                        pendingUserMessageIds.remove(userMessageId);
                    } else {
                        if (errorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedIntermediateError on " + callback + " for " + retMessage);
                            }
                            callback.receivedIntermediateError(command, errorDescription);
                        } else {
                            if (debug) {
                                log.debug("Calling receivedIntermediateResultString on " + callback + " for " + retMessage);
                            }
                            callback.receivedIntermediateResultString(command, retMessage.getResultString());
                        }
                    }

                }

                // an argument could be made that this belongs in the above if,
                // but I think this makes it a little more resilient to strange
                // errors
                if (pendingUserMessageIds.isEmpty()) {
                    callback.complete();

                    if (debug) {
                        log.debug("Removing porter message listener because pending list is empty: " + this);
                    }
                    this.removeListener();
                }
            }
        }

        private synchronized void handleUnknownReturn(long userMessageID, Object aResult) {
            // figure out what the command was
            String origCommand = "unknown-command";
            T command = pendingUserMessageIds.get(userMessageID);
            if (command != null) {
                origCommand = command.toString();
            }
            log.warn("received a message in the result vector that wasn't a PointData: " + 
                     aResult.getClass().toString() + "; original command was: " + origCommand);
        }
        
        public int getGroupMessageId() {
            return groupMessageId;
        }
        
        private void removeListener() {
            porterConnection.removeMessageListener(this);
            msgListeners.remove(this.callback);
        }
        
        public synchronized void cancelCommands(final LiteYukonUser user) {

            // log remaining commands as canceled
            for (T cmd : pendingUserMessageIds.values()) {
                log.info("Command canceled by user '" + user.getUsername() + "':" + cmd.toString());
            }
            
            // remove listener
            removeListener();
        }

        @Override
        public synchronized String toString() {
            ToStringCreator tsc = new ToStringCreator(this);
            tsc.append("groupMessageId", groupMessageId);
            tsc.append("callback", callback);
            tsc.append("pendingUserMessageIds", pendingUserMessageIds.keySet());
            return tsc.toString();
        }
    }

    public CommandResultHolder execute(T command, LiteYukonUser user)
            throws CommandCompletionException, PaoAuthorizationException {
        return execute(Collections.singletonList(command), user);
    }

    public CommandResultHolder execute(List<T> commands, LiteYukonUser user)
            throws CommandCompletionException, PaoAuthorizationException {
        WaitableCommandCompletionCallback callback = new WaitableCommandCompletionCallback();

        execute(commands, callback, user);

        try {
            callback.waitForCompletion(betweenResultsMaxDelay,totalMaxDelay);
            return callback;
        } catch (InterruptedException e) {
            throw new CommandCompletionException("Interrupted while block for command completion",
                                                 e);
        } catch (TimeoutException e) {
            throw new CommandCompletionException("Timed out while blocking for command completion",
                                                 e);
        }

    }

    public void execute(List<T> commands,
            final CommandCompletionCallback<? super T> callback, LiteYukonUser user)
            throws PaoAuthorizationException {

        log.debug("Executing " + commands.size() + " for " + callback);

        if (commands.isEmpty()) {
            log.debug("Skipping execution because there were no commands");
            callback.complete();
            return;
        }

        // build up a list of command requests
        final List<RequestHolder> commandRequests = new ArrayList<RequestHolder>(commands.size());
        int groupMessageId = RandomUtils.nextInt();
        for (T command : commands) {
            Request request = buildRequest(command);
            request.setGroupMessageID(groupMessageId);
            RequestHolder requestHolder = new RequestHolder();
            requestHolder.command = command;
            requestHolder.request = request;
            commandRequests.add(requestHolder);
        }

        // create listener
        CommandResultMessageListener messageListener = new CommandResultMessageListener(commandRequests,
                                                                                        callback, 
                                                                                        groupMessageId);
        msgListeners.put(callback, messageListener);
        
        log.debug("Addinging porter message listener: " + messageListener);
        porterConnection.addMessageListener(messageListener);

        boolean nothingWritten = true;
        try {
            // write requests
            for (RequestHolder requestHolder : commandRequests) {
                porterConnection.write(requestHolder.request);
                if (commandRequiresLogging(requestHolder.request)) {
                    logCommand(requestHolder.request, user);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Sent request to porter: " + requestHolder.request);
                }
                nothingWritten = false;
            }
        } finally {
            if (nothingWritten) {
                log.debug("Removing porter message listener because nothing was written: " + messageListener);
                messageListener.removeListener();
            }
        }
    }
    
    public long cancelExecution(CommandCompletionCallback<? super T> callback, LiteYukonUser user) {
        
        // get listener
        CommandResultMessageListener messageListener = msgListeners.get(callback);
        
        // run cancel command
        long commandsCanceled = porterRequestCancelService.cancelRequests(messageListener.getGroupMessageId());
        
        // cancel listener
        log.debug("Removing porter message listener due to cancel: " + messageListener);
        messageListener.cancelCommands(user);
        
        // cancel callback
        callback.cancel();
        
        return commandsCanceled;
    }
    
    private void logCommand(Request request, LiteYukonUser user) {
        new SystemLogHelper(PointTypes.SYS_PID_SYSTEM).log("Manual: " + request.getCommandString(), "ID: " + request.getDeviceID() + ", Route: " + request.getRouteID(), user);
    }

    private boolean commandRequiresLogging(Request request) {
        String commandString = request.getCommandString();
        Permission permission = commandPermissionConverter.getPermission(commandString);
        boolean result = loggableCommandPermissions.contains(permission);
        return result;
    }
    
    protected abstract Request buildRequest(T commandRequest);

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }

    public void setDefaultBackgroundPriority(int defaultBackgroundPriority) {
        this.defaultBackgroundPriority = defaultBackgroundPriority;
    }

    public void setDefaultForegroundPriority(int defaultForegroundPriority) {
        this.defaultForegroundPriority = defaultForegroundPriority;
    }
    
    private class RequestHolder {
        public Request request;
        public T command;
    }

    public Set<Permission> getLoggableCommandPermissions() {
        return loggableCommandPermissions;
    }

    public void setLoggableCommandPermissions(
            Set<Permission> loggableCommandPermissions) {
        this.loggableCommandPermissions = loggableCommandPermissions;
    }
    
    public void setCommandPermissionConverter(
            CommandPermissionConverter commandPermissionConverter) {
        this.commandPermissionConverter = commandPermissionConverter;
    }
    
    @Autowired
    public void setPorterRequestCancelService(
            PorterRequestCancelService porterRequestCancelService) {
        this.porterRequestCancelService = porterRequestCancelService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @ManagedAttribute
    public int getBetweenResultsMaxDelay() {
        return betweenResultsMaxDelay;
    }

    @ManagedAttribute
    public void setBetweenResultsMaxDelay(int betweenResultsMaxDelay) {
        this.betweenResultsMaxDelay = betweenResultsMaxDelay;
    }

    @ManagedAttribute
    public int getTotalMaxDelay() {
        return totalMaxDelay;
    }

    @ManagedAttribute
    public void setTotalMaxDelay(int totalMaxDelay) {
        this.totalMaxDelay = totalMaxDelay;
    }

}
