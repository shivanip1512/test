package com.cannontech.common.bulk.collection.device.dao;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;

/**
 * Dao for saving and loading DeviceCollectionPersistables.
 */
public interface DeviceCollectionPersistenceDao {
    /**
     * Saves the specified persistable.
     * @return The collectionId used to retrieve this persistable.
     */
    public int savePersistable(DeviceCollectionPersistable persistable);
    
    /**
     * Loads the persistable with the specified collectionId.
     */
    public DeviceCollectionPersistable loadPersistable(int collectionId);
    
    /**
     * Deletes the persistable with the specified collectionId.
     * @return True if the deletion was successful, otherwise false.
     */
    public boolean deletePersistable(int collectionId);
}