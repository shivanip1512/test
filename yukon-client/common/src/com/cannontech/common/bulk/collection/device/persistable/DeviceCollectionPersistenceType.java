package com.cannontech.common.bulk.collection.device.persistable;

/**
 * Describes the different Device Collection persistence types. Each of these corresponds to a particular
 * DeviceCollectionPersistable subclass.
 */
public enum DeviceCollectionPersistenceType {
    DEVICE_LIST,    //Stored as a list of individual device ids
    FIELD,          //Stored as one or more name/value pairs
    ;
}
