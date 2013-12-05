package com.cannontech.common.bulk.collection.device.persistable;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.pao.YukonPao;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

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
    public static DeviceCollectionById create(DeviceCollectionType collectionType, Iterable<? extends YukonPao> devices) {
        Iterable<Integer> deviceIds = Iterables.transform(devices, new Function<YukonPao, Integer>() {
            @Override
            public Integer apply(YukonPao pao) {
                return pao.getPaoIdentifier().getPaoId();
            }
        });
        return new DeviceCollectionById(collectionType, deviceIds);
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
