package com.cannontech.common.bulk.collection.device.service;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;

/**
 * Service to save and load DeviceCollections.
 */
public interface DeviceCollectionService {
    /**
     * Saves a DeviceCollection to the database.
     * @return The collectionId used to load this collection.
     */
    public int saveCollection(DeviceCollection collection);
    
    /**
     * Loads the DeviceCollection with the specified collectionId.
     */
    public DeviceCollection loadCollection(int collectionId);
    
    /**
     * Deletes the DeviceCollection with the specified collectionId from the database.
     */
    public boolean deleteCollection(int collectionId);
}
