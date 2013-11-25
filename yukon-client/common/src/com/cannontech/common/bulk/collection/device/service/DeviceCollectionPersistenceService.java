package com.cannontech.common.bulk.collection.device.service;

import com.cannontech.common.bulk.collection.device.DeviceCollection;

/**
 * Service to save and load DeviceCollections.
 */
public interface DeviceCollectionPersistenceService {
    /**
     * Saves a DeviceCollection to the database.
     * @return The collectionId used to load this collection.
     */
    public int saveCollection(DeviceCollection collection);
    
    /**
     * Loads the DeviceCollection with the specified collectionId.
     */
    public DeviceCollection loadCollection(int collectionId);
}
