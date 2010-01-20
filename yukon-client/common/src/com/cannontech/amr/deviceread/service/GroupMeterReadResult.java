package com.cannontech.amr.deviceread.service;

import java.util.Date;
import java.util.Set;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.MultipleDeviceResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;

public class GroupMeterReadResult implements Completable, ExceptionStatus, Comparable<GroupMeterReadResult>{

	private String key;
    private Set<? extends Attribute> attributes;
    private DeviceCollection deviceCollection;
    private DeviceCollection originalDeviceCollectionCopy;
    private MultipleDeviceResultHolder resultHolder;
    private GroupCommandCompletionCallback callback;
    private Date startTime;
    private CommandRequestExecutionType commandRequestExecutionType;
    private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier;
    
    private DeviceCollection successCollection;
    private DeviceCollection failureCollection;
    private DeviceCollection unsupportedCollection;
    
    public Set<? extends Attribute> getAttributes() {
		return attributes;
	}
    public void setAttributes(Set<? extends Attribute> attributes) {
		this.attributes = attributes;
	}
    public Date getStartTime() {
		return startTime;
	}
    public void setStartTime(Date startTime) {
		this.startTime = startTime;
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
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }
    public DeviceCollection getOriginalDeviceCollectionCopy() {
		return originalDeviceCollectionCopy;
	}
    public void setOriginalDeviceCollectionCopy(DeviceCollection originalDeviceCollectionCopy) {
		this.originalDeviceCollectionCopy = originalDeviceCollectionCopy;
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
    
    // success collection
    public DeviceCollection getSuccessCollection() {
        return successCollection;
    }
    public void setSuccessCollection(DeviceCollection successCollection) {
        this.successCollection = successCollection;
    }
    
    // fail collection
    public DeviceCollection getFailureCollection() {
        return failureCollection;
    }
    public void setFailureCollection(DeviceCollection failureCollection) {
        this.failureCollection = failureCollection;
    }
    
    // unsupported collection
    public DeviceCollection getUnsupportedCollection() {
		return unsupportedCollection;
	}
    public void setUnsupportedCollection(DeviceCollection unsupportedCollection) {
		this.unsupportedCollection = unsupportedCollection;
	}
    
    public GroupCommandCompletionCallback getCallback() {
        return callback;
    }
    public void setCallback(GroupCommandCompletionCallback callback) {
        this.callback = callback;
    }
    
    
    @Override
    public boolean isComplete() {
        return resultHolder.isComplete();
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
    
    @Override
    public int compareTo(GroupMeterReadResult o) {
    	
    	return this.getStartTime().compareTo(o.getStartTime()); 
    }
}
