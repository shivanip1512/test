package com.cannontech.common.device.commands;

import java.util.Date;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;

public abstract class GroupActionResult implements Completable, ExceptionStatus, Comparable<GroupActionResult> {
    protected String key;
    protected DeviceCollection deviceCollection;
    protected MultipleDeviceResultHolder resultHolder;
    protected GroupCommandCompletionCallback callback;
    protected Date startTime;
    protected DeviceRequestType commandRequestExecutionType;
    protected CommandRequestExecutionIdentifier commandRequestExecutionIdentifier;

    protected DeviceCollection successCollection;
    protected DeviceCollection failureCollection;
    protected DeviceCollection unsupportedCollection;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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

    public DeviceCollection getUnsupportedCollection() {
        return unsupportedCollection;
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

    public void setUnsupportedCollection(DeviceCollection unsupportedCollection) {
        this.unsupportedCollection = unsupportedCollection;
    }

    public void setSuccessCollection(DeviceCollection successCollection) {
        this.successCollection = successCollection;
    }

    public DeviceRequestType getCommandRequestExecutionType() {
        return commandRequestExecutionType;
    }

    public void setCommandRequestExecutionType(DeviceRequestType commandRequestExecutionType) {
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

    @Override
    public int compareTo(GroupActionResult o) {
        return this.getStartTime().compareTo(o.getStartTime());
    }
}