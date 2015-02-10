package com.cannontech.common.bulk.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.Completable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ArchiveAnalysisProfileReadResult implements Completable {
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private Multimap<SimpleDevice, CommandCallback> deviceCommandMap = ArrayListMultimap.create();
    private Set<SimpleDevice> devicesWithFailure = Sets.newHashSet();
    private boolean complete = false;
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup failureGroup;
    private List<String> errorList;
    private int devicesCompletedCount = 0;
    private int devicesSucceeded = 0;
    private int devicesFailed = 0;
    private int totalNumberOfDevicesToRead;
    
    public ArchiveAnalysisProfileReadResult(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao,
                                            DeviceGroupCollectionHelper deviceGroupCollectionHelper,
                                            StoredDeviceGroup successGroup,
                                            StoredDeviceGroup failureGroup,
                                            List<CommandRequestDevice> commandRequestDeviceList) {
        
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
        this.successGroup = successGroup;
        this.failureGroup = failureGroup;
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;;
        
        for(CommandRequestDevice request : commandRequestDeviceList) {
            deviceCommandMap.put(request.getDevice(), request.getCommandCallback());
        }
        totalNumberOfDevicesToRead = deviceCommandMap.keySet().size();
    }
    
    public void commandSucceeded(CommandRequestDevice request) {
        SimpleDevice device = request.getDevice();
        
        //remove from list of pending requests
        deviceCommandMap.remove(device, request.getCommandCallback());
        
        //see if we've finished all commands for that device
        if(deviceCommandMap.containsKey(device)) {
            //do nothing. more commands need to complete for this device
        } else {
            //check for previous failures
            if(devicesWithFailure.contains(device)) {
                deviceGroupMemberEditorDao.addDevices(failureGroup, device);
                devicesFailed++;
            } else {
                deviceGroupMemberEditorDao.addDevices(successGroup, device);
                devicesSucceeded++;
            }
            devicesCompletedCount++;
        }
    }
    
    public void commandFailed(CommandRequestDevice request) {
        SimpleDevice device = request.getDevice();
        
        //remove from list of pending requests
        deviceCommandMap.remove(device, request.getCommandCallback());
        
        //see if we've finished all commands for that device
        if(deviceCommandMap.containsKey(device)) {
            //mark device as having a failed request
            devicesWithFailure.add(device);
        } else {
            //all requests for device are complete. Add to failure list.
            deviceGroupMemberEditorDao.addDevices(failureGroup, device);
            devicesFailed++;
            devicesCompletedCount++;
        }
    }
    
    public void processingExceptionOccurred(String error) {
        if(errorList==null) errorList = Lists.newArrayList();
        errorList.add(error);
    }
    
    /**
     * returns null if there have been no errors
     */
    public List<String> getErrors() {
        return errorList;
    }
    
    public boolean isErrorOccurred() {
        return errorList!=null;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void setComplete() {
        complete = true;
    }

    public void setSuccessGroup(StoredDeviceGroup successGroup) {
        this.successGroup = successGroup;
    }

    public StoredDeviceGroup getSuccessGroup() {
        return successGroup;
    }

    public void setFailureGroup(StoredDeviceGroup failureGroup) {
        this.failureGroup = failureGroup;
    }

    public StoredDeviceGroup getFailureGroup() {
        return failureGroup;
    }
    
    public DeviceCollection getSuccessDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
    }
    
    public DeviceCollection getFailureDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
    }
    
    public int getCompletedCount() {
        return devicesCompletedCount;
    }
    
    public int getSucceededCount() {
        return devicesSucceeded;
    }
    
    public int getFailedCount() {
        return devicesFailed;
    }

    /**
     * Returns the total number of devices that need to be read.
     * This may differ from the total number of read commands that need to be sent out (as one
     * device may require multiple reads).
     * This may also differ from the total number of devices in the analysis (as some devices may
     * not require any reads).
     */
    public int getTotalCount() {
        return totalNumberOfDevicesToRead;
    }
}
