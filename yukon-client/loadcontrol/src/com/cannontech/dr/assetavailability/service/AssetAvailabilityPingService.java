package com.cannontech.dr.assetavailability.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;

/**
 * This service "pings" (reads asset availability data) from two-way devices in specified DR groupings - load group, 
 * program, control area or scenario. It also provides access to the results of these "ping" operations.
 */
public interface AssetAvailabilityPingService {
    
    /**
     * Attempt to read asset availability data from all two-way devices in the DR grouping specified by the 
     * paoIdentifier.
     */
    public void readDevicesInDrGrouping(PaoIdentifier paoIdentifier, LiteYukonUser user);
    
    /**
     * Get the latest read result for the DR grouping specified by the paoId.
     */
    public AssetAvailabilityReadResult getReadResult(int paoId);
    
    /**
     * Determines if there is an active (incomplete) ping result for the DR grouping specified by the paoId.
     */
    public boolean hasReadResult(int paoId);
}
