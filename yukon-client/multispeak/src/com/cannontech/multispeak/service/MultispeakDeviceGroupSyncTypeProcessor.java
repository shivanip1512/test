package com.cannontech.multispeak.service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.client.MultispeakVendor;

public interface MultispeakDeviceGroupSyncTypeProcessor {
    
    /**
     * Starts synchronization process.
     * 
     * @param deviceGroupSyncValue - {substationName} or {billingCycleName} based on synchronization type
     *        selected i.e MultispeakSyncTypeProcessorType enum value.
     * @return true if substationGroup or BillingCyle updated.
     */

    public boolean processMeterSync(MultispeakVendor mspVendor, String deviceGroupSyncValue, YukonMeter yukonMeter);

    public PersistedSystemValueKey getPersistedSystemValueKey();
}
