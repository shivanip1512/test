package com.cannontech.common.bulk.collection.device.persistable;

import java.util.Map;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.google.common.collect.ImmutableMap;

/**
 * A DeviceCollectionBase that contains one or more field name/value pairs.
 */
public final class DeviceCollectionByField implements DeviceCollectionBase {
    private final ImmutableMap<String, String> valueMap;
    private final DeviceCollectionType collectionType;
    
    /**
     * Create a new object with the specified map of field names and values.
     */
    public DeviceCollectionByField(DeviceCollectionType collectionType, Map<String, String> valueMap) {
        this.collectionType = collectionType;
        this.valueMap = ImmutableMap.copyOf(valueMap);
    }
    
    @Override
    public DeviceCollectionType getCollectionType() {
        return collectionType;
    }
    
    @Override
    public DeviceCollectionDbType getCollectionDbType() {
        return DeviceCollectionDbType.FIELD;
    }
    
    public ImmutableMap<String, String> getValueMap() {
        return valueMap;
    }
}
