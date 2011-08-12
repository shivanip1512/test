package com.cannontech.common.bulk.collection.device;

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

}