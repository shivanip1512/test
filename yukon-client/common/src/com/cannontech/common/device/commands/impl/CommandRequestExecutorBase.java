package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestBase;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.service.PorterRequestCancelService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

/**
 * Abstract base class for CommandRequestExecutor
 * @param <T> - Type of command request this executor will execute
 */
@ManagedResource
public abstract class CommandRequestExecutorBase<T extends CommandRequestBase> implements
        CommandRequestExecutor<T> {
    private BasicServerConnection porterConnection;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private CommandPermissionConverter commandPermissionConverter;
    private PorterRequestCancelService porterRequestCancelService;
    private Set<Permission> loggableCommandPermissions = new HashSet<Permission>();
    private ConfigurationSource configurationSource;
    private Executor executor;
    private CommandRequestExecutionDao commandRequestExecutionDao;
	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	private NextValueHelper nextValueHelper;
    
    private Map<CommandCompletionCallback<? super T>, CommandResultMessageListener> msgListeners = new HashMap<CommandCompletionCallback<? super T>, CommandResultMessageListener>();
    
    Logger log = YukonLogManager.getLogger(CommandRequestExecutorBase.class);
    private int betweenResultsMaxDelay = 60;
    private int totalMaxDelay = 180;
    private static int CANCEL_PRIORITY = 8;
    
    @PostConstruct
    public void initialize() {
        betweenResultsMaxDelay = configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_BETWEEN_RESULTS_MAX_DELAY", betweenResultsMaxDelay);
        totalMaxDelay = configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_TOTAL_MAX_DELAY", totalMaxDelay);
    }

    // COMMAND RESULT MESSAGE LISTENER
    private final class CommandResultMessageListener implements MessageListener {
        private final CommandCompletionCallback<? super T> callback;
        private Map<Long, T> pendingUserMessageIds;
        private int groupMessageId;
        private volatile boolean canceled = false;
        private CountDownLatch commandsAreWritingLatch = new CountDownLatch(1);
        private CommandRequestExecution commandRequestExecution;

        private CommandResultMessageListener(List<RequestHolder> requests,
                CommandCompletionCallback<? super T> callback,
                int groupMessageId,
                CommandRequestExecution commandRequestExecution) {
            
            this.callback = callback;
            this.groupMessageId = groupMessageId;
            this.commandRequestExecution = commandRequestExecution;
            
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
                
                // this is one of ours
                if (pendingUserMessageIds.containsKey(userMessageId)) {
                    
                	T command = pendingUserMessageIds.get(userMessageId);
                    if (debug) {
                        log.debug("Got return message " + retMessage + " for my " + command + " on " + this);
                    }
                    
                    // check the status message and create an error if necessary 
                    DeviceErrorDescription errorDescription = null;
                    int status = retMessage.getStatus();
                    if (status != 0) {
                        errorDescription = deviceErrorTranslatorDao.translateErrorCode(status);
                        errorDescription.setPorter(retMessage.getResultString());
                        if (debug) {
                            log.debug("Calling receivedError on " + callback + " for " + retMessage);
                        }
                    }
                    
                    // grab point data and give it to the callback
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

                    // last results
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
                        
                        // insert CommandRequestExecutionResult record
                    	saveCommandRequestExecutionResult(this.commandRequestExecution, command, status);
                        
                        pendingUserMessageIds.remove(userMessageId);
                        
                    // intermediate results
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
                	
                	// complete the callback
                    callback.complete();
                    
                    // complete the commandRequestExecution record
                    completeCommandRequestExecutionRecord(this.commandRequestExecution);

                    if (debug) {
                        log.debug("Removing porter message listener because pending list is empty: " + this);
                    }
                    this.removeListener();
                }
            }
        }
        
        private void completeCommandRequestExecutionRecord(CommandRequestExecution commandRequestExecution) {
        	
        	commandRequestExecution.setStopTime(new Date());
            commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        }
        
        private void saveCommandRequestExecutionResult(CommandRequestExecution commandRequestExecution, T command, int status) {
        	
        	CommandRequestExecutionResult commandRequestExecutionResult = new CommandRequestExecutionResult();
        	commandRequestExecutionResult.setCommandRequestExecutionId(commandRequestExecution.getId());
        	commandRequestExecutionResult.setCommand(command.getCommand());
        	commandRequestExecutionResult.setCompleteTime(new Date());
        	
        	if (status != 0) {
        		commandRequestExecutionResult.setErrorCode(status);
        	}
        	
        	applyIdsToCommandRequestExecutionResult(command, commandRequestExecutionResult);
            
            commandRequestExecutionResultDao.saveOrUpdate(commandRequestExecutionResult);
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
    

    // EXECUTE SINGLE, WAIT (wraps single command in list then calls execute-multiple-wait)
    public CommandResultHolder execute(T command, CommandRequestExecutionType type, LiteYukonUser user) throws CommandCompletionException {
        
        return execute(Collections.singletonList(command), type, user);
    }
    

    // EXECUTE MULTIPLE, WAIT (makes a waitable callback then calls execute-multiple-with-callback)
    public CommandResultHolder execute(List<T> commands, CommandRequestExecutionType type, LiteYukonUser user) throws CommandCompletionException {
        
        CollectingCommandCompletionCallback callback = new CollectingCommandCompletionCallback();
        WaitableCommandCompletionCallback<Object> waitableCallback = new WaitableCommandCompletionCallback<Object>(callback);

        CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = execute(commands, waitableCallback, type, user);
        callback.setCommandRequestExecutionIdentifier(commandRequestExecutionIdentifier);

        try {
            waitableCallback.waitForCompletion(betweenResultsMaxDelay,totalMaxDelay);
            return callback;
        } catch (InterruptedException e) {
            throw new CommandCompletionException("Interrupted while block for command completion", e);
        } catch (TimeoutException e) {
            throw new CommandCompletionException("Timed out while blocking for command completion", e);
        }
    }
    
    
    // EXECUTE MULTIPLE, CALLBACK (creates a one time use parameterDto and calls executeWithParameterDto)
    public CommandRequestExecutionIdentifier execute(final List<T> commands,
                                                     final CommandCompletionCallback<? super T> callback, 
                                                     CommandRequestExecutionType type, 
                                                     final LiteYukonUser user) {
    
        CommandRequestExecutionTemplate<T> executionTemplate = getExecutionTemplate(type, user);
        return executionTemplate.execute(commands, callback);
    }
    
    
    // EXECUTE MULTIPLE, CALLBACK, parameterDto
    public CommandRequestExecutionIdentifier executeWithParameterDto(final List<T> commands,
                                                                final CommandCompletionCallback<? super T> callback, 
                                                                CommandRequestExecutionParameterDto parameterDto) {

        log.debug("Executing " + commands.size() + " for " + callback);

        if (commands.isEmpty()) {
            log.debug("Skipping execution because there were no commands");
            callback.complete();
            return null;
        }
        
        // parameterDto
        final LiteYukonUser user = parameterDto.getUser();
        final boolean noqueue = parameterDto.isNoqueue();
        // This method also handles the overriding of command priorities. 
        final int priority = configurationSource.getInteger("OVERRIDE_PRIORITY_"+parameterDto.getType(), parameterDto.getPriority());
        
        // create CommandRequestExection record
        CommandRequestExecutionContextId contextId = parameterDto.getContextId();
        CommandRequestExecutionType type = parameterDto.getType();
        
        final CommandRequestExecution commandRequestExecution = new CommandRequestExecution();
        commandRequestExecution.setContextId(contextId.getId());
        commandRequestExecution.setStartTime(new Date());
        commandRequestExecution.setRequestCount(commands.size());
        commandRequestExecution.setCommandRequestExecutionType(type);
        commandRequestExecution.setUserName(user.getUsername());
        commandRequestExecution.setCommandRequestType(getCommandRequestType());
        
        commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = new CommandRequestExecutionIdentifier(commandRequestExecution.getId());
        
        // execute
        executor.execute(new Runnable() {
        	
        	public void run() {

		        // build up a list of command requests
		        final List<RequestHolder> commandRequests = new ArrayList<RequestHolder>(commands.size());
		        int groupMessageId = RandomUtils.nextInt();
		        
		        for (T command : commands) {
		            
		            // build basic request
		            Request request = new Request();
		            String commandStr = command.getCommand();
                    if (noqueue && !StringUtils.containsIgnoreCase(commandStr, " noqueue")) {
                        commandStr += " noqueue";
                    }
                    request.setCommandString(commandStr);
                    request.setGroupMessageID(groupMessageId);
                    request.setPriority(priority);
                    
		            // allow executor to adjust request (set deviceId, routeId, etc)
		            adjustRequest(request, command);
		            
		            // request holder
		            RequestHolder requestHolder = new RequestHolder();
		            requestHolder.command = command;
		            requestHolder.request = request;
		            commandRequests.add(requestHolder);
		        }
		
		        // create listener
		        CommandResultMessageListener messageListener = new CommandResultMessageListener(commandRequests,
		                                                                                        callback, 
		                                                                                        groupMessageId,
		                                                                                        commandRequestExecution);
		        
		        msgListeners.put(callback, messageListener);
		        
		        log.debug("Addinging porter message listener: " + messageListener);
		        porterConnection.addMessageListener(messageListener);
		
		        boolean nothingWritten = true;
		        boolean completeAndRemoveListener = false;
		        try {
		            // write requests
		        	log.debug("Starting commandRequests loop. groupMessageId = " + groupMessageId);
		            for (RequestHolder requestHolder : commandRequests) {
		            	
		            	if (!messageListener.isCanceled()) {
		            	
			                porterConnection.write(requestHolder.request);
			                nothingWritten = false;
			                
			                if (commandRequiresLogging(requestHolder.request)) {
			                    logCommand(requestHolder.request, user);
			                }
			                if (log.isDebugEnabled()) {
			                    log.debug("Sent request to porter: " + requestHolder.request);
			                }
			                
		            	} else {
		            		log.debug("Not sending request due to cancel: " + requestHolder.request);
		            	}
		            }
		            log.debug("Finished commandRequests loop. groupMessageId = " + groupMessageId);
		        
		        } catch (ConnectionException e) {
		        	
		        	callback.processingExceptionOccured("No porter connection.");
		        	completeAndRemoveListener = true;
		        	log.debug("Removing porter message listener because an exception occured: " + messageListener);
		        	
		        } catch (Exception e) {
		        	
		        	callback.processingExceptionOccured(e.getMessage());
		        	completeAndRemoveListener = true;
		        	log.debug("Removing porter message listener because an exception occured (" + e.getMessage() + "): " + messageListener);
		        	
		        } finally {
		            if (nothingWritten && !messageListener.isCanceled()) {
		            	completeAndRemoveListener = true;
		                log.debug("Removing porter message listener because nothing was written: " + messageListener);
		            }
		            
		            if (completeAndRemoveListener) {
		            	callback.complete();
		            	messageListener.removeListener();
		            }
		            
		            messageListener.getCommandsAreWritingLatch().countDown();
		            log.debug("Latch counted down. groupMessageId = " + groupMessageId);
		        }
        	}
        });
        
        return commandRequestExecutionIdentifier;
    }
    
    // CANCEL
    public long cancelExecution(CommandCompletionCallback<? super T> callback, LiteYukonUser user) {
        
        // get listener
        CommandResultMessageListener messageListener = msgListeners.get(callback);
        
        // The callback has already returned.
        if(messageListener == null) return 0;
        
        // cancel listener
        // - settings listener to canceled will cause the commandRequests write loop to stop sending
        //   commands and log them as unsent instead.
        messageListener.setCanceled();
        
        // wait for the listener latch to countdown, which will happen when it has finished it's commandRequests write loop
        log.debug("Awaiting latch countdown. groupMessageId = " + messageListener.getGroupMessageId());
        try {
        	if (!messageListener.getCommandsAreWritingLatch().await(5, TimeUnit.MINUTES)) {
        		log.error("Timeout waiting for latch, continue with cancelExecution anyway. groupMessageId = " + messageListener.getGroupMessageId());
        	} else {
        		log.debug("Latch await over, continue with cancelExecution. groupMessageId = " + messageListener.getGroupMessageId());
        	}
        } catch (InterruptedException e) {
        	log.error("Latch wait encountered InterruptedException. groupMessageId = " + messageListener.getGroupMessageId(), e);
        }

        // run cancel command
        log.debug("Sending cancel request. groupMessageId = " + messageListener.getGroupMessageId());
        long commandsCanceled = porterRequestCancelService.cancelRequests(messageListener.getGroupMessageId(), CANCEL_PRIORITY);
        
        // cancel callback
        log.debug("Calling callback. groupMessageId = " + messageListener.getGroupMessageId());
        callback.cancel();
        callback.complete();
        
        // remove listener
        log.debug("Removing porter message listener due to cancel: " + messageListener);
        messageListener.removeListener();
        
        return commandsCanceled;
    }
    
    // EXECUTION TEMPLATE
    public CommandRequestExecutionTemplate<T> getExecutionTemplate(CommandRequestExecutionType type, final LiteYukonUser user) {
        
        CommandRequestExecutionContextId contextId = new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        CommandRequestExecutionParameterDto parameterDto = new CommandRequestExecutionParameterDto(contextId, type, user);
        
        return new CommandRequestExecutionTemplateImpl(parameterDto);
    }
    
    private final class CommandRequestExecutionTemplateImpl implements CommandRequestExecutionTemplate<T> {
        
        private CommandRequestExecutionParameterDto parameterDto;

        private CommandRequestExecutionTemplateImpl(CommandRequestExecutionParameterDto parameterDto) {
            this.parameterDto = parameterDto;
        }
        
        @Override
        public CommandRequestExecutionContextId getContextId() {
            return this.parameterDto.getContextId();
        };
        
        @Override
        public CommandRequestExecutionIdentifier execute(List<T> commands, CommandCompletionCallback<? super T> callback) {
            return executeWithParameterDto(commands, callback, this.parameterDto);
        };

        @Override
        public CommandRequestExecutionIdentifier execute(List<T> commands,CommandCompletionCallback<? super T> callback, boolean noqueue) {
            return executeWithParameterDto(commands, callback, this.parameterDto.withNoqueue(noqueue));
        }
        
        @Override
        public CommandRequestExecutionIdentifier execute(List<T> commands,CommandCompletionCallback<? super T> callback,int priority) {
            return executeWithParameterDto(commands, callback, this.parameterDto.withPriority(priority));
        }
        
        @Override
        public CommandRequestExecutionIdentifier execute(List<T> commands,CommandCompletionCallback<? super T> callback, boolean noqueue, int priority) {
            return executeWithParameterDto(commands, callback, this.parameterDto.withNoqueue(noqueue).withPriority(priority));
        }
        
        @Override
        public String toString() {

            ToStringCreator tsc = new ToStringCreator(this);
            tsc.append("parameterDto", parameterDto.toString());
            return tsc.toString();
        }
    }

    // HELPERS
    private void logCommand(Request request, LiteYukonUser user) {
        new SystemLogHelper(PointTypes.SYS_PID_SYSTEM).log("Manual: " + request.getCommandString(), "ID: " + request.getDeviceID() + ", Route: " + request.getRouteID(), user);
    }

    private boolean commandRequiresLogging(Request request) {
        String commandString = request.getCommandString();
        Permission permission = commandPermissionConverter.getPermission(commandString);
        boolean result = loggableCommandPermissions.contains(permission);
        return result;
    }
    
    // ABSTRACT METHODS
    protected abstract void adjustRequest(Request request, T commandRequest);
    
    protected abstract CommandRequestType getCommandRequestType();
    
    protected abstract void applyIdsToCommandRequestExecutionResult(T commandRequest, CommandRequestExecutionResult commandRequestExecutionResult);

    
    // INJECTED DEPENDANCIES
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
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
    
    @Resource(name="longRunningExecutor")
    public void setExecutor(Executor executor) {
		this.executor = executor;
	}
    
    @Autowired
    public void setCommandRequestExecutionDao(
			CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
    
    @Autowired
    public void setCommandRequestExecutionResultDao(
			CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
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
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
