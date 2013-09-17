package com.cannontech.dr.assetavailability.ping;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;

/**
 * Strategies implementing this interface represent ways to "ping" different types of device for asset availability data.
 */
public interface AssetAvailabilityPingStrategy {
    /**
     * The type of devices this strategy handles.
     */
    public AssetPingStrategyType getType();
    
    /**
     * Returns the subset of the specified devices that this strategy can handle.
     */
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices);
    
    /**
     * Performs an asset availability "ping" on the specified devices. Results are stored in the recentResultsCache
     * under the specified resultId.
     */
    public void readDevices(Set<? extends YukonPao> devices, String resultId);
}
