package com.cannontech.common.bulk.collection.device.persistable;

import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;

/**
 * A database persistence object for device collections.
 * Multiple DeviceCollection types may use the same child type to store their data in the database.
 */
public interface DeviceCollectionBase {
    
    /** Gets the type of device collection this data object was derived from. */
    DeviceCollectionType getCollectionType();
    
    /** Gets the DB type of this object, which determines how it is stored in the DB. */
    DeviceCollectionDbType getCollectionDbType();
    
}