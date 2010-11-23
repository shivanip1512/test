package com.cannontech.common.bulk.collection.device;

public enum DeviceCollectionType {
    idList, 
    group, 
    deviceFilter, 
    fileUpload, 
    addressRange,
    ;
    
    public String getParameterName(String parameter) {
        return name() + "." + parameter;
    }
}
