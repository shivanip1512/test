package com.cannontech.multispeak.service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;

public interface MultispeakDeviceGroupSyncTypeProcessor {

    /**
     * process Meter Sync
     * 
     * @param mspVendor
     * @param mspServiceLocation
     * @param mspMeter
     * @param yukonMeter
     * @return
     */
    public boolean processMeterSync(MultispeakVendor mspVendor, ServiceLocation mspServiceLocation, Meter mspMeter,
            YukonMeter yukonMeter);

    /**
     * get Persisted System Value Key
     * 
     * @return
     */
    public PersistedSystemValueKey getPersistedSystemValueKey();
}
