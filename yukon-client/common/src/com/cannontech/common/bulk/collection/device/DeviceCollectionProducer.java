package com.cannontech.common.bulk.collection.device;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;

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
     * Get the DeviceCollectionBase representation of the DeviceCollection.
     */
    public DeviceCollectionBase getBaseFromCollection(DeviceCollection collection);
    
    /**
     * Get the DeviceCollection from the DeviceCollectionBase.
     */
    public DeviceCollection getCollectionFromBase(DeviceCollectionBase persistable);

}