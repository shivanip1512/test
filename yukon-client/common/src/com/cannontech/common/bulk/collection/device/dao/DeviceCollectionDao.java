package com.cannontech.common.bulk.collection.device.dao;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;

/**
 * Dao for saving and loading device collections.
 */
public interface DeviceCollectionDao {
    /**
     * Saves the specified device collection.
     * @return The collectionId used to retrieve this device collection.
     */
    public int saveCollection(DeviceCollectionBase collection);
    
    /**
     * Loads the device collection with the specified collectionId.
     */
    public DeviceCollectionBase loadCollection(int collectionId);
    
    /**
     * Deletes the device collection with the specified collectionId.
     * @return True if the deletion was successful, otherwise false.
     */
    public boolean deleteCollection(int collectionId);
}