package com.cannontech.common.bulk.service;

import com.cannontech.common.bulk.collection.DeviceCollection;

public class MassChangeFileInfo extends DeviceCollectionContainingFileInfo {

    String massChangeBulkFieldName = "";
    
    public MassChangeFileInfo(DeviceCollection deviceCollection, String massChangeBulkFieldName){
        super(deviceCollection);
        this.massChangeBulkFieldName = massChangeBulkFieldName;
    }
    
    public String getMassChangeBulkFieldName() {
        return massChangeBulkFieldName;
    }
}
