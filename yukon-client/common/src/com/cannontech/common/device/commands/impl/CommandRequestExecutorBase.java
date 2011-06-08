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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandPriority;
import com.cannontech.common.device.commands.CommandRequestBase;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.events.loggers.CommandRequestExecutorEventLogService;
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
	private CommandRequestExecutorEventLogService commandRequestExecutorEventLogService;
	private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;
    
    private Map<CommandCompletionCallback<? super T>, CommandResultMessageListener> msgListeners = new ConcurrentHashMap<CommandCompletionCallback<? super T>, CommandResultMessageListener>();
    
    private static final Logger log = YukonLogManager.getLogger(CommandRequestExecutorBase.class);
    private static final int CANCEL_PRIORITY = 8;

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
                    SpecificDeviceErrorDescription specificErrorDescription = null;
                    int status = retMessage.getStatus();
                    if (status != 0) {
                        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(status);
                        specificErrorDescription  = new SpecificDeviceErrorDescription(errorDescription, retMessage.getResultString());
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

                        if (specificErrorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedLastError on " + callback + " for " + retMessage);
                            }
                            callback.receivedLastError(command, specificErrorDescription);
                            
                            
                        } else {
                            log.debug("Calling receivedLastResultString on " + callback + " for " + retMessage);
                            callback.receivedLastResultString(command, retMessage.getResultString());
                            
                        }
                        
                        // insert CommandRequestExecutionResult record
                    	saveCommandRequestExecutionResult(this.commandRequestExecution, command, status, retMessage.getCommandString());
                        
                        pendingUserMessageIds.remove(userMessageId);
                        
                    // intermediate results
                    } else {
                        if (specificErrorDescription != null) {
                            if (debug) {
                                log.debug("Calling receivedIntermediateError on " + callback + " for " + retMessage);
                            }
                            callback.receivedIntermediateError(command, specificErrorDescription);
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
                    completeCommandRequestExecutionRecord(this.commandRequestExecution, CommandRequestExecutionStatus.COMPLETE);

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
    
    private void completeCommandRequestExecutionRecord(CommandRequestExecution commandRequestExecution, CommandRequestExecutionStatus executionStatus) {
    	
    	commandRequestExecution.setStopTime(new Date());
    	commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
        commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
    }
    
    private void saveCommandRequestExecutionResult(CommandRequestExecution commandRequestExecution, T command, int status, String returnMsgCommand) {
    	
    	CommandRequestExecutionResult commandRequestExecutionResult = new CommandRequestExecutionResult();
    	commandRequestExecutionResult.setCommandRequestExecutionId(commandRequestExecution.getId());
    	commandRequestExecutionResult.setCommand(returnMsgCommand);
    	commandRequestExecutionResult.setCompleteTime(new Date());
    	
    	if (status != 0) {
    		commandRequestExecutionResult.setErrorCode(status);
    	}
    	
    	applyIdsToCommandRequestExecutionResult(command, commandRequestExecutionResult);
        
        commandRequestExecutionResultDao.saveOrUpdate(commandRequestExecutionResult);
    }

    // EXECUTE SINGLE, WAIT (wraps single command in list then calls execute-multiple-wait)
    public CommandResultHolder execute(T command, DeviceRequestType type, LiteYukonUser user) throws CommandCompletionException {
        
        return execute(Collections.singletonList(command), type, user);
    }
    

    // EXECUTE MULTIPLE, WAIT (makes a waitable callback then calls execute-multiple-with-callback)
    public CommandResultHolder execute(List<T> commands, DeviceRequestType type, LiteYukonUser user) throws CommandCompletionException {
        
        CollectingCommandCompletionCallback callback = new CollectingCommandCompletionCallback();
        WaitableCommandCompletionCallback<Object> waitableCallback = waitableCommandCompletionCallbackFactory.createWaitable(callback);

        CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = execute(commands, waitableCallback, type, user);
        callback.setCommandRequestExecutionIdentifier(commandRequestExecutionIdentifier);

        try {
            waitableCallback.waitForCompletion();
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
                                                     DeviceRequestType type, 
                                                     final LiteYukonUser user) {
    
        CommandRequestExecutionTemplate<T> executionTemplate = getExecutionTemplate(type, user);
        return executionTemplate.execute(commands, callback);
    }
    
    
    // EXECUTE MULTIPLE, CALLBACK, parameterDto
    public CommandRequestExecutionIdentifier executeWithParameterDto(final List<T> commands,
                                                                final CommandCompletionCallback<? super T> callback, 
                                                                CommandRequestExecutionParameterDto parameterDto) {

        log.debug("Executing " + commands.size() + " for " + callback);

        // parameterDto
        final LiteYukonUser user = parameterDto.getUser();
        final boolean noqueue = parameterDto.isNoqueue();
        // This method also handles the overriding of command priorities. 
        int commandPriority;
        try{
            commandPriority = configurationSource.getInteger("OVERRIDE_PRIORITY_"+parameterDto.getType(), parameterDto.getPriority());
            if(!CommandPriority.isCommandPriorityValid(commandPriority))
                throw new IllegalArgumentException("Command priorities must be between "+CommandPriority.minPriority+" and "+CommandPriority.maxPriority);
        } catch (IllegalArgumentException e) {
            log.warn("System has recieved a new priority for "+parameterDto.getType()+", but cannot use the value because it is invalid.  The system will revert to using the default priority value until a valid priority is supplied.  Please fix the priority value and try again.",e);
            commandPriority = parameterDto.getPriority();
        }
        final int priority = commandPriority;
        
        // create CommandRequestExection record
        final CommandRequestExecutionContextId contextId = parameterDto.getContextId();
        final DeviceRequestType type = parameterDto.getType();
        
        final CommandRequestExecution commandRequestExecution = new CommandRequestExecution();
        commandRequestExecution.setContextId(contextId.getId());
        commandRequestExecution.setStartTime(new Date());
        commandRequestExecution.setRequestCount(commands.size());
        commandRequestExecution.setCommandRequestExecutionType(type);
        commandRequestExecution.setUserName(user.getUsername());
        commandRequestExecution.setCommandRequestType(getCommandRequestType());
        commandRequestExecution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
        
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
		
		        boolean exceptionOccured = false;
		        boolean nothingWritten = true;
		        boolean completeAndRemoveListener = false;
		        RequestHolder currentRequestHolder = null;
		        try {
		            // write requests
		        	log.debug("Starting commandRequests loop. groupMessageId = " + groupMessageId);
		            for (RequestHolder requestHolder : commandRequests) {
		            	
		            	currentRequestHolder = requestHolder;
		            	
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
		        	exceptionOccured = true;
		        	String error = "No porter connection.";
		        	callback.processingExceptionOccured(error);
		        	completeAndRemoveListener = true;
		        	log.debug("Removing porter message listener because an exception occured: " + messageListener);
		        	
		        	commandRequestExecutorEventLogService.commandFailedToTransmit(commandRequestExecution.getId(), contextId.getId(), type, currentRequestHolder.request.getCommandString(), error, user);
		        	
		        } catch (Exception e) {
		        	
		        	exceptionOccured = true;
		        	callback.processingExceptionOccured(e.getMessage());
		        	completeAndRemoveListener = true;
		        	log.debug("Removing porter message listener because an exception occured (" + e.getMessage() + "): " + messageListener);
		        	
		        	commandRequestExecutorEventLogService.commandFailedToTransmit(commandRequestExecution.getId(), contextId.getId(), type, currentRequestHolder.request.getCommandString(), e.getMessage(), user);
		        	
		        } finally {
		            if (nothingWritten && !messageListener.isCanceled()) {
		            	completeAndRemoveListener = true;
		                log.debug("Removing porter message listener because nothing was written: " + messageListener);
		            }
		            
		            if (completeAndRemoveListener) {
		            	callback.complete();
		            	messageListener.removeListener();
		            	completeCommandRequestExecutionRecord(commandRequestExecution, exceptionOccured ? CommandRequestExecutionStatus.FAILED : CommandRequestExecutionStatus.COMPLETE);
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
        
        // wait for the listener latch to countdown, which will happen when it has finished its commandRequests write loop
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
    public CommandRequestExecutionTemplate<T> getExecutionTemplate(DeviceRequestType type, final LiteYukonUser user) {
        
        CommandRequestExecutionContextId contextId = new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        CommandRequestExecutionParameterDto parameterDto = new CommandRequestExecutionParameterDto(contextId, type, user);
        int priority = CommandRequestExecutionDefaults.getPriority(type);
        boolean noqueue = CommandRequestExecutionDefaults.isNoqueue(type);
        parameterDto = parameterDto.withPriority(priority).withNoqueue(noqueue);
        
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
    
    @Resource(name="globalScheduledExecutor")
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
    public int getPendingRequestCount() {
        return msgListeners.size();
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setCommandRequestExecutorEventLogService(CommandRequestExecutorEventLogService commandRequestExecutorEventLogService) {
		this.commandRequestExecutorEventLogService = commandRequestExecutorEventLogService;
	}

    @Autowired
    public void setWaitableCommandCompletionCallbackFactory(
            WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory) {
        this.waitableCommandCompletionCallbackFactory = waitableCommandCompletionCallbackFactory;
    }
}
