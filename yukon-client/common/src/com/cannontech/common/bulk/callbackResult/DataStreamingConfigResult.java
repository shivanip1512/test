package com.cannontech.common.bulk.callbackResult;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.model.SimpleDevice;

public class DataStreamingConfigResult implements BackgroundProcessResultHolder {
    
    private String resultsId;
    private boolean complete;
    private String exceptionReason;
    private Date startTime;
    private Date stopTime;
    private DeviceCollection allDevicesCollection;
    private DeviceCollection successDeviceCollection;
    private DeviceCollection failureDeviceCollection;
    private DeviceCollection canceledDeviceCollection;
    private DeviceCollection unsupportedDeviceCollection;
    
    private DataStreamingConfigCallback configCallback;
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

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    public void addError(SimpleDevice meter, SpecificDeviceErrorDescription error) {
        errors.put(meter, error);
    }
    
    public Map<SimpleDevice, SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }
    
    public boolean isExceptionOccured() {
        return !StringUtils.isEmpty(exceptionReason);
    }

    public String getExceptionReason() {
        return exceptionReason;
    }
    
    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }
    
    public CommandCompletionCallback<CommandRequestDevice> getCommandCompletionCallback() {
        return commandCompletionCallback;
    }

    public void setCommandCompletionCallback(CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback) {
        this.commandCompletionCallback = commandCompletionCallback;
    }

    public int getCompletedCount() {
        int completed = getSuccessCount() + getFailureCount() + getCanceledCount() + getUnsupportedCount();
        return completed;
    }

    public int getTotalCount() {
        return allDevicesCollection.getDeviceCount();
    }

    @Override
    public int getSuccessCount() {
        return getSuccessDeviceCollection().getDeviceCount();
    }

    public int getUnsupporteddCount() {
        return getUnsupportedDeviceCollection().getDeviceCount();
    }
   
    public DataStreamingConfigCallback getConfigCallback() {
        return configCallback;
    }

    public void setConfigCallback(DataStreamingConfigCallback configCallback) {
        this.configCallback = configCallback;
    }

    public DeviceCollection getUnsupportedCollection() {
        return getUnsupportedDeviceCollection();
    }

    public void setUnsupportedCollection(DeviceCollection unsupportedCollection) {
        this.setUnsupportedDeviceCollection(unsupportedCollection);
    }

    public int getUnsupportedCount() {
       return getUnsupportedDeviceCollection().getDeviceCount();
    }

    public int getCanceledCount() {
        return getCanceledDeviceCollection().getDeviceCount();
    }
    
    public int getFailureCount() {
        return failureDeviceCollection.getDeviceCount();
    }

    @Override
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
        return BackgroundProcessTypeEnum.DATA_STREAMING;
    }

    @Override
    public List<ProcessorCallbackException> getProcessingExceptionList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Integer, ProcessorCallbackException> getProcessingExceptionRowNumberMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getProcessingExceptionCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getTotalItems() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isSuccessfull() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProcessingFailed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Exception getFailedException() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getStopTime() {
        return stopTime;
    }

    @Override
    public boolean isSuccessDevicesSupported() {
        return true;
    }

    @Override
    public DeviceCollection getSuccessDeviceCollection() {
        return successDeviceCollection;
    }

    @Override
    public boolean isFailureDevicesSupported() {
        return true;
    }

    @Override
    public DeviceCollection getFailureDeviceCollection() {
        return failureDeviceCollection;
    }

    @Override
    public boolean isFailureReasonsListSupported() {
        return false;
    }

    @Override
    public boolean isFailureFileSupported() {
        return false;
    }

    public String getResultsId() {
        return resultsId;
    }

    public void setResultsId(String resultsId) {
        this.resultsId = resultsId;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setSuccessDeviceCollection(DeviceCollection successDeviceCollection) {
        this.successDeviceCollection = successDeviceCollection;
    }

    public void setFailureDeviceCollection(DeviceCollection failureDeviceCollection) {
        this.failureDeviceCollection = failureDeviceCollection;
    }

    public DeviceCollection getCanceledDeviceCollection() {
        return canceledDeviceCollection;
    }

    public void setCanceledDeviceCollection(DeviceCollection canceledDeviceCollection) {
        this.canceledDeviceCollection = canceledDeviceCollection;
    }

    public DeviceCollection getUnsupportedDeviceCollection() {
        return unsupportedDeviceCollection;
    }

    public void setUnsupportedDeviceCollection(DeviceCollection unsupportedDeviceCollection) {
        this.unsupportedDeviceCollection = unsupportedDeviceCollection;
    }
}