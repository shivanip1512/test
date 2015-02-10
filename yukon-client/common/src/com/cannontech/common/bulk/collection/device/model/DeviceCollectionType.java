package com.cannontech.common.bulk.collection.device.model;

/**
 * Enum representing the different types of device collection.
 * NOTE: This class currently doesn't conform to formatting standards for enums (uppercase, underscore-separated-words).
 * This should be fixed at some point in the future. However, there are numerous string references to these types in
 * jsps, as well as references in the database that will need to be modified accordingly and thoroughly tested.
 */
public enum DeviceCollectionType {
    idList, 
    group, 
    groups, 
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
