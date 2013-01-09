package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionRetryCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CommandRequestRetryExecutor<T> {

    private CommandRequestExecutor<T> commandRequestExecutor = null;
    private int retryCount = 0;
    private Date stopRetryAfterDate = null;
    private Integer turnOffQueuingAfterRetryCount = null;
    private boolean cancelPending = false;
    
    private Logger log = YukonLogManager.getLogger(CommandRequestRetryExecutor.class);
    
    // CONSTRUCTORS
    public CommandRequestRetryExecutor(CommandRequestExecutor<T> commandRequestExecutor,
                                       RetryParameters retryParameters) {
        
        this.commandRequestExecutor = commandRequestExecutor;
        this.retryCount = retryParameters.getRetryCount();
        this.stopRetryAfterDate = retryParameters.getStopRetryAfterDate();
        this.turnOffQueuingAfterRetryCount = retryParameters.getTurnOffQueuingAfterRetryCount();
    }
    
    public CommandRequestRetryExecutor(CommandRequestExecutor<T> commandRequestExecutor, int retryCount) {
        
        this.commandRequestExecutor = commandRequestExecutor;
        this.retryCount = retryCount;
    }
    
    // EXECUTE
    public CommandRequestExecutionObjects<T> execute(List<T> commands, 
                                                  CommandCompletionCallback<? super T> callback,
                                                  DeviceRequestType type,
                                                  LiteYukonUser user) {
        
        CommandRequestExecutionTemplate<T> template = this.commandRequestExecutor.getExecutionTemplate(type, user);
        RetryCallback retryCallback = new RetryCallback(this.commandRequestExecutor, callback, this.retryCount, this.stopRetryAfterDate, this.turnOffQueuingAfterRetryCount, template);
        
        log.debug("Starting intial execution attempt using retry executor: contextId=" + template.getContextId().getId() + " retryCount=" + retryCount + " stopRetryAfterDate=" + stopRetryAfterDate + " turnOffQueuingAfterRetryCount=" + turnOffQueuingAfterRetryCount);
        template.execute(commands, retryCallback);
        
        CommandRequestExecutionObjects<T> executionObjects = new CommandRequestExecutionObjects<T>(commandRequestExecutor, retryCallback, template.getContextId());
        return executionObjects;
    }
    
    
    
    // RETRY CALLBACK
    private class RetryCallback implements CommandCompletionRetryCallback<T> {
        
        private CommandRequestExecutor<T> commandRequestExecutor;
        private CommandCompletionCallback<? super T> delegateCallback;
        private int retryCount;
        private Date stopRetryAfterDate;
        private Integer turnOffQueuingAfterRetryCount;
        private CommandRequestExecutionTemplate<T> executionTemplate;
        
        private int initialRetryCount;
        private String retryDescription;
        
        private List<FailedCommandAndError> failedCommands = new ArrayList<FailedCommandAndError>();
        
        public RetryCallback(CommandRequestExecutor<T> commandRequestExecutor,
                             CommandCompletionCallback<? super T> delegateCallback,
                             int retryCount,
                             Date stopRetryAfterDate,
                             Integer turnOffQueuingAfterRetryCount,
                             CommandRequestExecutionTemplate<T> executionTemplate) {
            
            this.commandRequestExecutor = commandRequestExecutor;
            this.delegateCallback = delegateCallback;
            this.retryCount = retryCount;
            this.stopRetryAfterDate = stopRetryAfterDate;
            this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
            this.executionTemplate = executionTemplate;
            
            this.initialRetryCount = retryCount;
            this.retryDescription = getRetryDesciption(retryCount, retryCount);
        }
        
        
        private RetryCallback(int initialRetryCount,
                              CommandRequestExecutor<T> commandRequestExecutor,
                              CommandCompletionCallback<? super T> delegateCallback,
                              int retryCount,
                              Date stopRetryAfterDate,
                              Integer turnOffQueuingAfterRetryCount,
                              CommandRequestExecutionTemplate<T> executionTemplate) {
            
            this.commandRequestExecutor = commandRequestExecutor;
            this.delegateCallback = delegateCallback;
            this.retryCount = retryCount;
            this.stopRetryAfterDate = stopRetryAfterDate;
            this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
            this.executionTemplate = executionTemplate;
            
            this.initialRetryCount = initialRetryCount;
            this.retryDescription = getRetryDesciption(retryCount, initialRetryCount);
            
        }

        @Override
        public void complete() {
        	
        	int thisRetryNumber = (initialRetryCount - retryCount);
        	int nextRetryNumber = thisRetryNumber + 1;
        	log.debug("Completed request. initialRetryCount = " + initialRetryCount + ", retryNumber = " + thisRetryNumber + ", nextRetryNumber = " + nextRetryNumber);
            
            // no more retry OR time up OR no more failures
        	// (or execution was cancelled)
            // ok to call complete() on delegate callback
            boolean noMoreRetrys = retryCount == 0;
            boolean timeUp = stopRetryAfterDate != null && stopRetryAfterDate.compareTo(new Date()) <= 0;
            boolean noMoreFails = failedCommands.size() == 0 && !noMoreRetrys && !timeUp;
            
            if (cancelPending ||
            	noMoreRetrys || 
                timeUp ||
                noMoreFails) {
                
                log.debug("Stop retry excutor after " + retryDescription + ". contextId= " + executionTemplate.getContextId().getId() + 
                		  ". Reason: noMoreRetrys=" + noMoreRetrys + " timeUp=" + timeUp + " noMoreFails=" + noMoreFails + " cancelPending=" + cancelPending +
                          ". " + failedCommands.size() + " failed commands remain and will be handled by delegate callback: " + delegateCallback);
                
                // these won't be retried after all, send to delegate
                for (FailedCommandAndError failedCommand : this.failedCommands) {
                    delegateCallback.receivedLastError(failedCommand.getCommand(), failedCommand.getError());
                }
                
                // complete delegate and get out
                delegateCallback.complete();
                return;
            }
            
            // start new execution of failed commands with reduced retry count, same context
            log.debug("Running another retry executor after " + retryDescription + " for " +  failedCommands.size() + " failed requests. contextId= " + executionTemplate.getContextId().getId() + ". ");
            
            // turnOffQueuing?
            boolean turnOffQueuing = turnOffQueuingAfterRetryCount != null && thisRetryNumber >= turnOffQueuingAfterRetryCount;
            if (turnOffQueuing) {
                log.debug("Retry executor will be executed with 'noqueue' after " + retryDescription + ". turnOffQueuingAfterRetryCount=" + turnOffQueuingAfterRetryCount + " contextId= " + executionTemplate.getContextId().getId() + ". ");
            }
            
            RetryCallback retryCallback = new RetryCallback(initialRetryCount, commandRequestExecutor, delegateCallback, retryCount - 1, 
                                                            stopRetryAfterDate, turnOffQueuingAfterRetryCount, executionTemplate);
            
            // re-run template with failed commands
            List<T> commands = new ArrayList<T>();
            for (FailedCommandAndError failedCommand : failedCommands) {
                commands.add(failedCommand.getCommand());
            }
            
            executionTemplate.execute(commands, retryCallback, turnOffQueuing ? true : false);
        }
        
        private String getRetryDesciption(int currentRetryCount, int initialRetryCount) {
            
            if (currentRetryCount == initialRetryCount) {
                return "initial execution (no retries ran yet)";
            }
            return "retry " + (initialRetryCount - currentRetryCount) + "/" + initialRetryCount;
        }


        @Override
        public void receivedLastError(T command, SpecificDeviceErrorDescription error) {

            // no more retry
            if (retryCount <= 0) {
                delegateCallback.receivedLastError(command, error);
                return;
            }
            
            // add to failedCommands
            failedCommands.add(new FailedCommandAndError(command, error));
        }

        
        
        // UNINTERESTING, PASS THROUGH TO DELEGATE
        @Override
        public void processingExceptionOccured(String reason) {
            retryCount = 0;
            delegateCallback.processingExceptionOccured(reason);
        }
        
        @Override
        public void receivedLastResultString(T command, String value) {
            delegateCallback.receivedLastResultString(command, value);
        }
        
        @Override
        public void receivedValue(T command, PointValueHolder value) {
            delegateCallback.receivedValue(command, value);
        }
        
        @Override
        public void receivedIntermediateError(T command, SpecificDeviceErrorDescription error) {
            delegateCallback.receivedIntermediateError(command, error);
            
        }
        
        @Override
        public void receivedIntermediateResultString(T command, String value) {
            delegateCallback.receivedIntermediateResultString(command, value);
        }
        
        @Override
        public void cancel() {
        	//This will prevent any further retries
        	cancelPending = true;
        	//Cancel the current execution
        	delegateCallback.cancel();
        }
    }
    
    private class FailedCommandAndError {
        
        private T command;
        private SpecificDeviceErrorDescription error;
        
        public FailedCommandAndError(T command, SpecificDeviceErrorDescription error) {
            this.command = command;
            this.error = error;
        }
        
        public T getCommand() {
            return this.command;
        }
        public SpecificDeviceErrorDescription getError() {
            return this.error;
        }
    }
}
