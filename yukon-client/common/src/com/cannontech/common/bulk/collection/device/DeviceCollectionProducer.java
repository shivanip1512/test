package com.cannontech.common.bulk.collection.device;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;

/**
 * Factory used to get a new instance of a device collection based on request
 * parameters
 */
public interface DeviceCollectionProducer extends DeviceCollectionFactory {

    /**
     * Method used to get the type of collection that this factory will produce
     * @return Type of collection
     */
    public DeviceCollectionType getSupportedType();
    
    /**
     * Get the persistable representation of the collection.
     */
    public DeviceCollectionPersistable getPersistableFromCollection(DeviceCollection collection);
    
    /**
     * Get the collection from the persistable.
     */
    public DeviceCollection getCollectionFromPersistable(DeviceCollectionPersistable persistable);

}