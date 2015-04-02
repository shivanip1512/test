package com.cannontech.amr.disconnect.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.Cancelable;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;

public class DisconnectResult implements Completable, ExceptionStatus, Cancelable {
    
    private DisconnectCommand command;
    private boolean complete;
    private String key;
    private String exceptionReason;
    private CommandRequestExecution commandRequestExecution;

    private DeviceCollection allDevicesCollection;
    private DeviceCollection armedCollection;
    private DeviceCollection connectedCollection;
    private DeviceCollection disconnectedCollection;
    private DeviceCollection unsupportedCollection;
    private DeviceCollection notConfiguredCollection;
    private DeviceCollection failedCollection;
    private DeviceCollection canceledCollection;
    
    private DisconnectCallback disconnectCallback;
    private CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback;
    
    //This map contains failed devices
    private final Map<SimpleDevice, SpecificDeviceErrorDescription> errors = new ConcurrentHashMap<>(100, .75f, 1);
    

    //This map contains successful devices
    private final Map<SimpleDevice, Instant> timestamps = new ConcurrentHashMap<>(100, .75f, 1);
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void addTimestamp(SimpleDevice device, Instant timestamp) {
        if (timestamp != null) {
            timestamps.put(device, timestamp);
        }
    }
    
    public Instant getTimestamp(SimpleDevice device) {
        return timestamps.get(device);
    }
    
    public void complete() {
        complete = true;
    }
    
    public DeviceCollection getAllDevicesCollection() {
        return allDevicesCollection;
    }

    public void setAllDevicesCollection(DeviceCollection allDevicesCollection) {
        this.allDevicesCollection = allDevicesCollection;
    }

    public DeviceCollection getArmedCollection() {
        return armedCollection;
    }

    public void setArmedCollection(DeviceCollection armedCollection) {
        this.armedCollection = armedCollection;
    }

    public DeviceCollection getConnectedCollection() {
        return connectedCollection;
    }

    public void setConnectedCollection(DeviceCollection connectedCollection) {
        this.connectedCollection = connectedCollection;
    }

    public DeviceCollection getDisconnectedCollection() {
        return disconnectedCollection;
    }

    public void setDisconnectedCollection(DeviceCollection disconnectedCollection) {
        this.disconnectedCollection = disconnectedCollection;
    }

    public DeviceCollection getUnsupportedCollection() {
        return unsupportedCollection;
    }

    public void setUnsupportedCollection(DeviceCollection unsupportedCollection) {
        this.unsupportedCollection = unsupportedCollection;
    }

    public DeviceCollection getNotConfiguredCollection() {
        return notConfiguredCollection;
    }

    public void setNotConfiguredCollection(DeviceCollection notConfiguredCollection) {
        this.notConfiguredCollection = notConfiguredCollection;
    }

    public DeviceCollection getFailedCollection() {
        return failedCollection;
    }

    public void setFailedCollection(DeviceCollection failedCollection) {
        this.failedCollection = failedCollection;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void addError(SimpleDevice meter, SpecificDeviceErrorDescription error) {
        errors.put(meter, error);
    }
    
    public Map<SimpleDevice, SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }

    @Override
    public boolean isExceptionOccured() {
        return !StringUtils.isEmpty(exceptionReason);
    }

    @Override
    public String getExceptionReason() {
        return exceptionReason;
    }
    
    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public DisconnectCommand getCommand() {
        return command;
    }

    public void setCommand(DisconnectCommand command) {
        this.command = command;
    }

    public DisconnectCallback getDisconnectCallback() {
        return disconnectCallback;
    }

    public void setDisconnectCallback(DisconnectCallback disconnectCallback) {
        this.disconnectCallback = disconnectCallback;
    }

    public CommandCompletionCallback<CommandRequestDevice> getCommandCompletionCallback() {
        return commandCompletionCallback;
    }

    public void setCommandCompletionCallback(CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback) {
        this.commandCompletionCallback = commandCompletionCallback;
    }

    public DeviceCollection getCanceledCollection() {
        return canceledCollection;
    }

    public void setCanceledCollection(DeviceCollection canceledCollection) {
        this.canceledCollection = canceledCollection;
    }

    public CommandRequestExecution getCommandRequestExecution() {
        return commandRequestExecution;
    }

    public void setCommandRequestExecution(CommandRequestExecution commandRequestExecution) {
        this.commandRequestExecution = commandRequestExecution;
    }

    @Override
    public boolean isCanceled() {
        return disconnectCallback.isCanceled();
    }
    
    /**
     * Returns the count of the devices that responded
     */
    public int getCompletedCount() {
        return (int) (failedCollection.getDeviceCount() 
                      + armedCollection.getDeviceCount()
                      + connectedCollection.getDeviceCount()
                      + disconnectedCollection.getDeviceCount());
    }
    
    /**
     * Returns the count of all the devices
     */
    public int getTotalCount() {
        return (int) allDevicesCollection.getDeviceCount();
    }
    
    /**
     * Returns the count of the devices that succeeded
     */
    public int getSuccessCount() {
        return (int) (armedCollection.getDeviceCount()
                      + connectedCollection.getDeviceCount() 
                      + disconnectedCollection.getDeviceCount());
    }
    
    /**
     * Returns the count of the devices that failed
     */
    public int getFailedCount() {
        return (int) failedCollection.getDeviceCount();
    }
    
    /**
     * Returns the count of the devices that the command was not sent to
     */
    public int getNotAttemptedCount() {
        return (int) (unsupportedCollection.getDeviceCount() 
                + notConfiguredCollection.getDeviceCount() 
                + canceledCollection.getDeviceCount());
    }
    
}