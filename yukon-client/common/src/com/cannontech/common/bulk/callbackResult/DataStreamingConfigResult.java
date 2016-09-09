package com.cannontech.common.bulk.callbackResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;

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
    private int configId;
    
    private StoredDeviceGroup allDevicesGroup;
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup failedGroup;
    private StoredDeviceGroup canceledGroup ;
    private StoredDeviceGroup unsupportedGroup;
    
    private DataStreamingConfigCallback configCallback;
    private CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback;
    private CommandRequestExecution execution;
    
    public DataStreamingConfigResult(TemporaryDeviceGroupService tempDeviceGroupService,
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        startTime = new Date();
        allDevicesGroup = tempDeviceGroupService.createTempGroup();
        successGroup = tempDeviceGroupService.createTempGroup();
        failedGroup = tempDeviceGroupService.createTempGroup();
        canceledGroup = tempDeviceGroupService.createTempGroup();
        unsupportedGroup = tempDeviceGroupService.createTempGroup();
        setAllDevicesCollection(deviceGroupCollectionHelper.buildDeviceCollection(allDevicesGroup));
        setSuccessDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(successGroup));
        setFailureDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        setCanceledDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));
        setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
    }
  
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void complete() {
        stopTime = new Date();
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
        return  getFailureCount();
    }

    @Override
    public int getTotalItems() {
        return allDevicesCollection.getDeviceCount();
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
        return true;
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

    public void setExecution(CommandRequestExecution execution) {
        this.execution = execution;
    }

    public CommandRequestExecution getExecution() {
        return execution;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }
    
    public StoredDeviceGroup getAllDevicesGroup() {
        return allDevicesGroup;
    }

    public StoredDeviceGroup getSuccessGroup() {
        return successGroup;
    }

    public StoredDeviceGroup getFailedGroup() {
        return failedGroup;
    }

    public StoredDeviceGroup getCanceledGroup() {
        return canceledGroup;
    }

    public StoredDeviceGroup getUnsupportedGroup() {
        return unsupportedGroup;
    }
}