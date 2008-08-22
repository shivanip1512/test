package com.cannontech.common.bulk.service;

import com.cannontech.common.bulk.collection.DeviceCollection;

public class DeviceCollectionContainingFileInfo extends BulkFileInfo {
    
    private DeviceCollection deviceCollection = null;
    
    public DeviceCollectionContainingFileInfo(DeviceCollection deviceCollection){
        super(null, true);
        this.deviceCollection = deviceCollection;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
}