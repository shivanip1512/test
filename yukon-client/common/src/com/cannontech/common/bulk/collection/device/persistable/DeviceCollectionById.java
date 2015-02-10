package com.cannontech.common.bulk.collection.device.persistable;

import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.google.common.collect.ImmutableList;

/**
 * A DeviceCollectionBase that stores a list of device ids.
 */
public final class DeviceCollectionById implements DeviceCollectionBase {
    
    private final ImmutableList<Integer> deviceIds;
    private final DeviceCollectionType collectionType;
    
    /**
     * Create a new object with the specified device ids.
     * To create an object from YukonPaos instead, use the create() method.
     */
    public DeviceCollectionById(DeviceCollectionType collectionType, Iterable<Integer> deviceIds) {
        this.collectionType = collectionType;
        this.deviceIds = ImmutableList.copyOf(deviceIds);
    }
    
    /**
     * Create a new object with the specified pao ids.
     */
    public static DeviceCollectionById create(DeviceCollectionType type, Iterable<? extends YukonPao> devices) {
        return new DeviceCollectionById(type, PaoUtils.asPaoIdList(devices));
    }
    
    @Override
    public DeviceCollectionType getCollectionType() {
        return collectionType;
    }
    
    @Override
    public DeviceCollectionDbType getCollectionDbType() {
        return DeviceCollectionDbType.DEVICE_LIST;
    }
    
    public ImmutableList<Integer> getDeviceIds() {
        return deviceIds;
    }
    
}