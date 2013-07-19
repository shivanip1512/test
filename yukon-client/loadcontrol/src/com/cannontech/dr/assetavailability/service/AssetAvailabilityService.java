package com.cannontech.dr.assetavailability.service;

import java.util.Collection;
import java.util.Set;

import com.cannontech.dr.assetavailability.AssetAvailability;
import com.cannontech.dr.assetavailability.service.impl.NoInventoryException;

public interface AssetAvailabilityService {
    /**
     * @return The AssetAvailability for the specified device.
     * @throws NoInventoryException If the device has no associated inventory.
     */
    public AssetAvailability getAssetAvailability(int deviceId) throws NoInventoryException;
    
    /**
     * @return The AssetAvailability for the specified devices. An AssetAvailability is returned for
     * each deviceId. However, if the asset availability cannot be retrieved (e.g. no associated
     * inventory), the object will only contain the deviceId and null values.
     */
    public Set<AssetAvailability> getAssetAvailability(Collection<Integer> deviceIds);
    
}
