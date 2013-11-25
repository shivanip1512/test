package com.cannontech.common.bulk.collection.device.persistable;

import java.util.Map;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.google.common.collect.ImmutableMap;

/**
 * A DeviceCollectionPersistable that contains one or more field name/value pairs.
 */
public final class FieldBasedCollectionPersistable implements DeviceCollectionPersistable {
    private final ImmutableMap<String, String> valueMap;
    private final DeviceCollectionType collectionType;
    
    /**
     * Create a new persistable with the specified map of field names and values.
     */
    public FieldBasedCollectionPersistable(DeviceCollectionType collectionType, Map<String, String> valueMap) {
        this.collectionType = collectionType;
        this.valueMap = ImmutableMap.copyOf(valueMap);
    }
    
    @Override
    public DeviceCollectionType getCollectionType() {
        return collectionType;
    }
    
    @Override
    public DeviceCollectionPersistenceType getPersistenceType() {
        return DeviceCollectionPersistenceType.FIELD;
    }
    
    public ImmutableMap<String, String> getValueMap() {
        return valueMap;
    }
}
