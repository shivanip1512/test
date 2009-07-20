package com.cannontech.common.device.commands;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;

public class GroupCommandResult implements Completable, CancelStatus, ExceptionStatus {
    private String key;
    private String command;
    private DeviceCollection deviceCollection;
    private MultipleDeviceResultHolder resultHolder;
    private GroupCommandCompletionCallback callback;
    private CommandRequestExecutionType commandRequestExecutionType;
    private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier;
    
    private DeviceCollection successCollection;
    private DeviceCollection failureCollection;
    
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }
    public MultipleDeviceResultHolder getResultHolder() {
        return resultHolder;
    }
    public void setResultHolder(MultipleDeviceResultHolder resultHolder) {
        this.resultHolder = resultHolder;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    
    public DeviceCollection getFailureCollection() {
        return failureCollection;
    }
    
    public DeviceCollection getSuccessCollection() {
        return successCollection;
    }
    
    public GroupCommandCompletionCallback getCallback() {
        return callback;
    }
    public void setCallback(GroupCommandCompletionCallback callback) {
        this.callback = callback;
    }
    public void setFailureCollection(DeviceCollection failureCollection) {
        this.failureCollection = failureCollection;
    }
    
    public void setSuccessCollection(DeviceCollection successCollection) {
        this.successCollection = successCollection;
    }
    
    public CommandRequestExecutionType getCommandRequestExecutionType() {
		return commandRequestExecutionType;
	}
    public void setCommandRequestExecutionType(CommandRequestExecutionType commandRequestExecutionType) {
		this.commandRequestExecutionType = commandRequestExecutionType;
	}
    public CommandRequestExecutionIdentifier getCommandRequestExecutionIdentifier() {
		return commandRequestExecutionIdentifier;
	}
    public void setCommandRequestExecutionIdentifier(CommandRequestExecutionIdentifier commandRequestExecutionIdentifier) {
		this.commandRequestExecutionIdentifier = commandRequestExecutionIdentifier;
	}
    
    @Override
    public boolean isComplete() {
        return resultHolder.isComplete();
    }
  
    @Override
    public boolean isCanceled() {
        return resultHolder.isCanceled();
    }
    
    @Override
    public boolean isExceptionOccured() {
    	return callback.isExceptionOccured();
    }
    
    @Override
    public String getExceptionReason() {
    	return callback.getExceptionReason();
    }
    
    public boolean isSuccessfullyComplete() {
    	return callback.isComplete() && !(callback.isCanceled() || callback.isExceptionOccured());
    }
    
    public boolean isAborted() {
    	return callback.isCanceled() || callback.isExceptionOccured();
    }
}
