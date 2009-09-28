package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionRetryCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionContext;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CommandRequestRetryExecutor<T> {

    private CommandRequestExecutor<T> commandRequestExecutor = null;
    private int retryCount = 0;
    private Date stopRetryAfterDate = null;
    private Integer turnOffQueuingAfterRetryCount = null;
    
    private Logger log = YukonLogManager.getLogger(CommandRequestRetryExecutor.class);
    
    // CONSTRUCTORS
    public CommandRequestRetryExecutor(CommandRequestExecutor<T> commandRequestExecutor,
                                       int retryCount,
                                       Date stopRetryAfterDate,
                                       Integer turnOffQueuingAfterRetryCount) {
        
        this.commandRequestExecutor = commandRequestExecutor;
        this.retryCount = retryCount;
        this.stopRetryAfterDate = stopRetryAfterDate;
        this.turnOffQueuingAfterRetryCount = turnOffQueuingAfterRetryCount;
    }
    
    public CommandRequestRetryExecutor(CommandRequestExecutor<T> commandRequestExecutor, int retryCount) {
        
        this.commandRequestExecutor = commandRequestExecutor;
        this.retryCount = retryCount;
    }
    
    // EXECUTE
    public CommandRequestExecutionContext execute(List<T> commands, 
                                                  CommandCompletionCallback<? super T> callback,
                                                  CommandRequestExecutionType type,
                                                  LiteYukonUser user) {
        
        CommandRequestExecutionTemplate<T> template = this.commandRequestExecutor.getExecutionTemplate(type, user);
        RetryCallback retryCallback = new RetryCallback(this.commandRequestExecutor, callback, this.retryCount, this.stopRetryAfterDate, this.turnOffQueuingAfterRetryCount, template);
        
        log.debug("++++++++++ Starting intial execution attempt using retry executor: contextId= " + template.getContext().getId() + " retryCount=" + retryCount + " stopRetryAfterDate=" + stopRetryAfterDate + " turnOffQueuingAfterRetryCount=" + turnOffQueuingAfterRetryCount);
        template.execute(commands, retryCallback);
        
        return template.getContext();
    }
    
    
    
    // RERTY CALLBACK
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
            
            // no more retry OR time up OR no more failures
            // ok to call complete() on delegate callback
            boolean noMoreRetrys = this.retryCount == 0;
            boolean timeUp = this.stopRetryAfterDate != null && this.stopRetryAfterDate.compareTo(new Date()) <= 0;
            boolean noMoreFails = this.failedCommands.size() == 0 && !noMoreRetrys && !timeUp;
            
            if (noMoreRetrys || 
                timeUp ||
                noMoreFails) {
                
                log.debug("++++++++++ Stop retry excutor after " + this.retryDescription + ". contextId= " + this.executionTemplate.getContext().getId() + ". Reason: noMoreRetrys=" + noMoreRetrys + " timeUp=" + timeUp + " noMoreFails=" + noMoreFails +
                          ". " + this.failedCommands.size() + " failed commands remain and will be handled by delegate callback: " + this.delegateCallback);
                
                // these won't be retried after all, send to delegate
                for (FailedCommandAndError failedCommand : this.failedCommands) {
                    this.delegateCallback.receivedLastError(failedCommand.getCommand(), failedCommand.getError());
                }
                
                // complete delegate and get out
                this.delegateCallback.complete();
                return;
            }
            
            // start new execution of failed commands with reduced retry count, same context
            log.debug("++++++++++ Running another retry executor after " + this.retryDescription + " for " +  this.failedCommands.size() + " failed requests. contextId= " + this.executionTemplate.getContext().getId() + ". ");
            
            boolean turnOffQueing = this.turnOffQueuingAfterRetryCount != null &&  this.turnOffQueuingAfterRetryCount > this.retryCount;
            if (turnOffQueing) {
                log.debug("++++++++++ Retry executor will be executed with 'noqueue' after " + this.retryDescription + ". turnOffQueuingAfterRetryCount=" + this.turnOffQueuingAfterRetryCount + " contextId= " + this.executionTemplate.getContext().getId() + ". ");
                
                this.executionTemplate.setIsNoqueue(true);
            }
            
            RetryCallback retryCallback = new RetryCallback(this.initialRetryCount, this.commandRequestExecutor, this.delegateCallback, this.retryCount - 1, 
                                                            this.stopRetryAfterDate, this.turnOffQueuingAfterRetryCount, this.executionTemplate);
            
            // re-run template with failed commands
            List<T> commands = new ArrayList<T>();
            for (FailedCommandAndError failedCommand : this.failedCommands) {
                commands.add(failedCommand.getCommand());
            }
            this.executionTemplate.execute(commands, retryCallback);
        }
        
        private String getRetryDesciption(int currentRetryCount, int initialRetryCount) {
            
            if (currentRetryCount == initialRetryCount) {
                return "initial execution (no retries ran yet)";
            }
            return "retry " + (initialRetryCount - currentRetryCount) + "/" + initialRetryCount;
        }


        @Override
        public void receivedLastError(T command, DeviceErrorDescription error) {

            // no more retry
            if (this.retryCount <= 0) {
                this.delegateCallback.receivedLastError(command, error);
                return;
            }
            
            // add to failedCommands
            this.failedCommands.add(new FailedCommandAndError(command, error));
        }

        
        
        // UNINTERESTING, PASS THROUGH TO DELEGATE
        @Override
        public void processingExceptionOccured(String reason) {
            this.retryCount = 0;
            this.delegateCallback.processingExceptionOccured(reason);
        }
        
        @Override
        public void receivedLastResultString(T command, String value) {
            this.delegateCallback.receivedLastResultString(command, value);
        }
        
        @Override
        public void receivedValue(T command, PointValueHolder value) {
            this.delegateCallback.receivedValue(command, value);
        }
        
        @Override
        public void receivedIntermediateError(T command, DeviceErrorDescription error) {
            this.delegateCallback.receivedIntermediateError(command, error);
            
        }
        
        @Override
        public void receivedIntermediateResultString(T command, String value) {
            this.delegateCallback.receivedLastResultString(command, value);
        }
        
        @Override
        public void cancel() {
            this.delegateCallback.cancel();
        }
    }
    
    private class FailedCommandAndError {
        
        private T command;
        private DeviceErrorDescription error;
        
        public FailedCommandAndError(T command, DeviceErrorDescription error) {
            this.command = command;
            this.error = error;
        }
        
        public T getCommand() {
            return this.command;
        }
        public DeviceErrorDescription getError() {
            return this.error;
        }
    }
}
