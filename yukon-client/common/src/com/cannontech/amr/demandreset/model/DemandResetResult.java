package com.cannontech.amr.demandreset.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.Completable;

public class DemandResetResult implements Completable, Comparable<DemandResetResult>{
        
    private String key;
        
    private CommandRequestExecution verificationExecution;
    private CommandRequestExecution initiatedExecution;
    
    private String exceptionReason;
    private boolean complete;

    private DeviceCollection allDevicesCollection;
    private DeviceCollection confirmedCollection;
    private DeviceCollection unconfirmedCollection;
    private DeviceCollection unsupportedCollection;
    private DeviceCollection failedCollection;
    private DeviceCollection canceledCollection;
    
    private final Set<CommandCompletionCallback<CommandRequestDevice>> commandCompletionCallbacks =
        new HashSet<CommandCompletionCallback<CommandRequestDevice>>();
    private DemandResetCallback demandResetCallback;
    private boolean cancellable;
    
    //contains error
    private final Map<SimpleDevice, SpecificDeviceErrorDescription> errors = new ConcurrentHashMap<>(100, .75f, 1);
    
    //contains successful devices
    private final Map<SimpleDevice, Instant> timestamps = new ConcurrentHashMap<>(100, .75f, 1);
    
    
    public void addTimestamp(SimpleDevice device, Instant timestamp) {
        if (timestamp != null) {
            timestamps.put(device, timestamp);
        }
    }
    
    public Instant getTimestamp(SimpleDevice device) {
        return timestamps.get(device);
    }
    
    public DeviceCollection getAllDevicesCollection() {
        return allDevicesCollection;
    }

    public void setAllDevicesCollection(DeviceCollection allDevicesCollection) {
        this.allDevicesCollection = allDevicesCollection;
    }

    public DeviceCollection getUnsupportedCollection() {
        return unsupportedCollection;
    }

    public void setUnsupportedCollection(DeviceCollection unsupportedCollection) {
        this.unsupportedCollection = unsupportedCollection;
    }

    public DeviceCollection getFailedCollection() {
        return failedCollection;
    }

    public void setFailedCollection(DeviceCollection failedCollection) {
        this.failedCollection = failedCollection;
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
    
    public SpecificDeviceErrorDescription getError(SimpleDevice meter) {
        return errors.get(meter);
    }
        
    public void addErrors(Map<SimpleDevice, SpecificDeviceErrorDescription> errors) {
        errors.putAll(errors);
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

    public DeviceCollection getCanceledCollection() {
        return canceledCollection;
    }

    public void setCanceledCollection(DeviceCollection canceledCollection) {
        this.canceledCollection = canceledCollection;
    }

    /**
     * Returns the count of the devices that responded
     */
    public int getCompletedCount() {
        return getFailedCollection().getDeviceCount() 
                      + getConfirmedCollection().getDeviceCount()
                      + getUnconfirmedCollection().getDeviceCount();
    }
    
    /**
     * Returns the count of all the devices
     */
    public int getTotalCount() {
        return getAllDevicesCollection().getDeviceCount();
    }
    
    /**
     * Returns the count of the devices that succeeded
     */
    public int getSuccessCount() {
        return getConfirmedCollection().getDeviceCount()  + getUnconfirmedCollection().getDeviceCount();
    }
    
    /**
     * Returns the count of the devices that failed
     */
    public int getFailedCount() {
        return getFailedCollection().getDeviceCount();
    }
    
    /**
     * Returns the count of the devices that the command was not sent to
     */
    public int getNotAttemptedCount() {
        return getUnsupportedCollection().getDeviceCount() + getCanceledCollection().getDeviceCount();
    }

    
    public boolean isCanceled() {
        return demandResetCallback == null || demandResetCallback.isCanceled();
    }

    public DeviceCollection getConfirmedCollection() {
        return confirmedCollection;
    }

    public void setConfirmedCollection(DeviceCollection confirmedCollection) {
        this.confirmedCollection = confirmedCollection;
    }

    public DeviceCollection getUnconfirmedCollection() {
        return unconfirmedCollection;
    }

    public void setUnconfirmedCollection(DeviceCollection unconfirmedCollection) {
        this.unconfirmedCollection = unconfirmedCollection;
    }

    public CommandRequestExecution getVerificationExecution() {
        return verificationExecution;
    }

    public void setVerificationExecution(CommandRequestExecution verificationExecution) {
        this.verificationExecution = verificationExecution;
    }

    public CommandRequestExecution getInitiatedExecution() {
        return initiatedExecution;
    }

    public void setInitiatedExecution(CommandRequestExecution initiatedExecution) {
        this.initiatedExecution = initiatedExecution;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }

  
    public DemandResetCallback getDemandResetCallback() {
        return demandResetCallback;
    }

    public void setDemandResetCallback(DemandResetCallback demandResetCallback) {
        this.demandResetCallback = demandResetCallback;
    }

    public Set<CommandCompletionCallback<CommandRequestDevice>> getCommandCompletionCallbacks() {
        return commandCompletionCallbacks;
    }

    public void addCommandCompletionCallbacks(Set<CommandCompletionCallback<CommandRequestDevice>> callbacks) {
        if (callbacks != null) {
            this.commandCompletionCallbacks.addAll(callbacks);
        }
    }

    public boolean cancellable() {
        if (initiatedExecution == null) {
            return false;
        }
        return cancellable && verificationExecution != null
               && verificationExecution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    @Override
    public int compareTo(DemandResetResult o) {
        return -initiatedExecution.getStartTime().compareTo(o.getInitiatedExecution().getStartTime());
    }
}
