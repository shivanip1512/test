package com.cannontech.common.bulk.collection.device.persistable;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;

/**
 * A database persistence object for device collections. Multiple DeviceCollection types may use the same persistable 
 * type to store their data in the database.
 */
public interface DeviceCollectionPersistable {
    /**
     * Gets the type of device collection this persistable was derived from.
     */
    public DeviceCollectionType getCollectionType();
    
    /**
     * Gets the persistence type of this object, which determines how it is stored in the DB.
     */
    public DeviceCollectionPersistenceType getPersistenceType();
}
