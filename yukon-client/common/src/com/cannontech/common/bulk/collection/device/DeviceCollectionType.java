package com.cannontech.common.bulk.collection.device;

public enum DeviceCollectionType {
    idList, 
    group, 
    deviceFilter, 
    fileUpload, 
    addressRange,
    archiveDataAnalysis, 
    memory,
    ;
    
    /**
     * Generates a "full" parameter name in the form of "type.parameter", usable for retrieving that parameter from a
     * device collection.
     */
    public String getParameterName(String parameter) {
        return name() + "." + parameter;
    }
}
