package com.cannontech.common.bulk.service;

import com.cannontech.common.bulk.collection.DeviceCollection;

public class MassChangeFileInfo extends BulkFileInfo {
    
    private DeviceCollection deviceCollection = null;
    String massChangeBulkFieldName = "";
    
    public MassChangeFileInfo(DeviceCollection deviceCollection, String massChangeBulkFieldName){
        super(null, true);
        this.deviceCollection = deviceCollection;
        this.massChangeBulkFieldName = massChangeBulkFieldName;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    
    public String getMassChangeBulkFieldName() {
        return massChangeBulkFieldName;
    }
}