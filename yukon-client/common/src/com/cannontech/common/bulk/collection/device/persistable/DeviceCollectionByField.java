package com.cannontech.common.bulk.collection.device.persistable;

import java.util.Set;

import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.google.common.collect.ImmutableSet;

/**
 * A DeviceCollectionBase that contains one or more field name/value pairs.
 */
public final class DeviceCollectionByField implements DeviceCollectionBase {
    
    private final ImmutableSet<DeviceCollectionField> fields;
    private final DeviceCollectionType collectionType;
    
    /**
     * Create a new object with the specified map of field names and values.
     */
    public DeviceCollectionByField(DeviceCollectionType collectionType, Set<DeviceCollectionField> fields) {
        this.collectionType = collectionType;
        this.fields = ImmutableSet.copyOf(fields);
    }
    
    @Override
    public DeviceCollectionType getCollectionType() {
        return collectionType;
    }
    
    @Override
    public DeviceCollectionDbType getCollectionDbType() {
        return DeviceCollectionDbType.FIELD;
    }
    
    public ImmutableSet<DeviceCollectionField> getFields() {
        return fields;
    }
    
}