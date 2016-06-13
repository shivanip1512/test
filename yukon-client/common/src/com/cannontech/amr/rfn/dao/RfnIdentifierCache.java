package com.cannontech.amr.rfn.dao;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;

public interface RfnIdentifierCache {

    /** 
     * Attempts to load and cache the paoId for the specified RfnManufacturerModel and serial.
     * Returns null if not found.
     */
    Integer getPaoIdFor(RfnManufacturerModel mm, String serial);

    /**
     * Invalidates the cache entry for the specified paoId.
     * Causes any existing RfnIdentifier association to be reloaded if requested.
     */
    void invalidatePaoId(int paoId);

    /**
     * Updates the cache entry for the specified paoId.
     * To be used only by the service generating the DBChange - all other changes should be handled via the cache's DBChangeListener. 
     */
    void updatePaoId(int paoId, RfnIdentifier rfnIdentifier);
    
}
